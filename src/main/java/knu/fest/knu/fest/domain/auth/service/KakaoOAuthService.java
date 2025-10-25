package knu.fest.knu.fest.domain.auth.service;

import knu.fest.knu.fest.domain.auth.controller.dto.kakao.KakaoTokenResponse;
import knu.fest.knu.fest.domain.auth.controller.dto.kakao.KakaoUserInfo;
import knu.fest.knu.fest.domain.auth.controller.dto.request.KakaoLoginRequest;
import knu.fest.knu.fest.domain.auth.controller.dto.response.KakaoLoginResponse;
import knu.fest.knu.fest.domain.auth.controller.dto.response.KakaoLoginUrlResponse;
import knu.fest.knu.fest.domain.auth.repository.AuthRepository;
import knu.fest.knu.fest.domain.user.entity.Provider;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.entity.UserRole;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import knu.fest.knu.fest.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoOAuthService {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.authorization-uri}")
    private String kakaoAuthorizationUri;

    @Value("${kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;

    public KakaoLoginUrlResponse getKakaoAuthorizationUrl() {
        // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-code-additional-consent
        String authorizationUrl =  UriComponentsBuilder.fromUriString(kakaoAuthorizationUri)
            .queryParam("response_type", "code")
            .queryParam("client_id", kakaoClientId)
            .queryParam("redirect_uri", kakaoRedirectUri) // TODO - 배포 시 변경
            // https://developers.kakao.com/docs/latest/ko/kakaologin/utilize#scope-user
            .queryParam("scope", "profile_nickname,profile_image,account_email")
            .toUriString();

        return KakaoLoginUrlResponse.builder()
                .authorizationUrl(authorizationUrl)
                .build();
    }

    public KakaoLoginResponse processKakaoLogin(KakaoLoginRequest request) {
        // 사용자 정보 요청
        KakaoUserInfo userInfo = getUserInfo(getAccessToken(request.code()));
        log.info("Received kakao user info: {}", userInfo);

        // 신규 유저인지 확인
        String kakaoUserEmail = userInfo.id() + "_" + userInfo.kakaoAccount().email();
        Optional<User> userOpt = userRepository.findByEmail(kakaoUserEmail);

        if (userOpt.isEmpty()) {
            // 신규 유저라면 계정 생성
            User newUser = createNewUser(userInfo);
            userRepository.save(newUser);

            return handleLogin(newUser, true);
        } else {
            User existingUser = userOpt.get();
            return handleLogin(existingUser, false);
        }
    }

    // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
    private String getAccessToken(String code) {
        log.info("processing code: {}", code);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", kakaoClientId);
        formData.add("redirect_uri", kakaoRedirectUri);
        formData.add("code", code);
        return webClient.post()
            .uri(kakaoTokenUri)
            .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(KakaoTokenResponse.class)
            .transform(mono -> applyKakaoApiErrorHandling(mono, "access token"))
            .map(KakaoTokenResponse::accessToken)
            .block(); // 동기 요청
    }

    // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
    private KakaoUserInfo getUserInfo(String accessToken) {
        return webClient.post()
            .uri(kakaoUserInfoUri)
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
            // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#propertykeys
            .body(BodyInserters.fromFormData("property_keys", "[\"kakao_account.email\",\"kakao_account.profile\"]"))
            .retrieve()
            .bodyToMono(KakaoUserInfo.class)
            .transform(mono -> applyKakaoApiErrorHandling(mono, "user info"))
            .block(); // 동기 요청
    }

    private User createNewUser(KakaoUserInfo userInfo) {
        KakaoUserInfo.KakaoAccount kakaoAccount = Objects.requireNonNull(
            userInfo.kakaoAccount(),
            "KakaoAccount cannot be null"
        );
        KakaoUserInfo.KakaoAccount.Profile profile = Objects.requireNonNull(
            kakaoAccount.profile(),
            "Profile cannot be null"
        );
        String nickname = Objects.requireNonNull(
            profile.nickname(),
            "Nickname cannot be null as it is a mandatory consent item in Kakao Login"
        );
        String kakaoUserEmail = userInfo.id() + "_" + userInfo.kakaoAccount().email();
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        String profileImageUrl = profile.profileImageUrl();
        log.info("received kakao data. email: {} nickname: {} profileImageUrl: {}", kakaoUserEmail, nickname, profileImageUrl);
        // Profile image URL can be null as it's an optional consent item in Kakao Login
        if (profileImageUrl == null){
            profileImageUrl = getDefaultImage();
        }
        return User.builder()
            .email(kakaoUserEmail)
            .password(password)
            .nickname(nickname)
            .profileImageUrl(profileImageUrl)
            .role(UserRole.USER) // default 계정 : 후원자
            .provider(Provider.KAKAO)
            .build();
    }

    private KakaoLoginResponse handleLogin(User account, boolean isNewUser) {
        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessTokenWithAccountEntity(account);
        String refreshToken = jwtTokenProvider.createRefreshTokenWithAccountEntity(account);

        // refresh 토큰 저장
        authRepository.saveRefreshToken(
            account.getId(),
            refreshToken,
            JwtTokenProvider.REFRESH_TOKEN_VALIDITY
        );

        // Jwt 및 로그인 정보 전달
        return KakaoLoginResponse.builder()
            .accountId(account.getId())
            .userRole(account.getRole().getAuthority())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .isNewUser(isNewUser)
            .build();
    }
    private <T> Mono<T> applyKakaoApiErrorHandling(Mono<T> mono, String operationName) {
        return mono.onErrorResume(error -> {
            log.error("Error occurred while retrieving Kakao {}: {}", operationName, error.getMessage());
            return Mono.empty(); // 에러 발생 시 빈 Mono 반환
        });
    }
    private String getDefaultImage(){
        if (Math.random() < 0.5) {
            return "https://";
        } else {
            return "https://";
        }
    }
}
