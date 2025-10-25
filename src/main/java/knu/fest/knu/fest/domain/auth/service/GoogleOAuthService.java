package knu.fest.knu.fest.domain.auth.service;

import knu.fest.knu.fest.domain.auth.controller.dto.google.GoogleUserInfo;
import knu.fest.knu.fest.domain.auth.controller.dto.request.GoogleLoginRequest;
import knu.fest.knu.fest.domain.auth.controller.dto.response.GoogleLoginResponse;
import knu.fest.knu.fest.domain.auth.controller.dto.response.GoogleLoginUrlResponse;
import knu.fest.knu.fest.domain.auth.repository.AuthRepository;
import knu.fest.knu.fest.domain.user.entity.Provider;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.entity.UserRole;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import knu.fest.knu.fest.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleOAuthService {

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Value("${google.authorization-uri}")
    private String googleAuthorizationUrl;

    @Value("${google.token-uri}")
    private String googleTokenUrl;

    @Value("${google.user-info-uri}")
    private String googleUserInfoUrl;


    private final WebClient webClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;

    public GoogleLoginUrlResponse getGoogleLoginUrl() {

        String authorizationUrl = googleAuthorizationUrl
                + "?client_id=" + googleClientId
                + "&redirect_uri=" + googleRedirectUri
                + "&response_type=code"
                + "&scope=openid%20email%20profile"
                + "&access_type=offline";

        return GoogleLoginUrlResponse.builder()
                .authorizationUrl(authorizationUrl)
                .build();
    }

    public GoogleLoginResponse processGoogleLogin(GoogleLoginRequest request) {
        GoogleUserInfo userInfo = getUserInfo(getAccessToken(request.code()));
        log.info("Recieved google user info: {}", userInfo);

        System.out.println(userInfo.id());

        String googleUserEmail = userInfo.id() + "_" + userInfo.email();
        Optional<User> userOp = userRepository.findByEmail(googleUserEmail);

        if (userOp.isEmpty()) {
            // 신규 유저라면 계정 생성
            User newUser = createNewUser(userInfo);
            userRepository.save(newUser);

            return handleLogin(newUser, true);
        } else {
            User existingUser = userOp.get();
            return handleLogin(existingUser, false);
        }
    }

    public String getAccessToken(String code) {
        Map<String, String> response = webClient.post()
                .uri(googleTokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("code", code)
                        .with("client_id", googleClientId)
                        .with("client_secret", googleClientSecret)
                        .with("redirect_uri", googleRedirectUri)
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();

        if (response != null && response.containsKey("access_token")) {
            log.info("Google Token Response: {}", response);
            return response.get("access_token");
        } else {
            throw new RuntimeException("Failed to get access token from Google: " + response);
        }
    }

    private User createNewUser(GoogleUserInfo userInfo) {
        String googleUserEmail = userInfo.id() + "_" + userInfo.email();
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        String nickname = userInfo.name();
        String profileImageUrl = userInfo.picture();

        return User.builder()
                .email(googleUserEmail)
                .password(password)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .role(UserRole.USER)
                .provider(Provider.GOOGLE)
                .build();
    }

    private GoogleLoginResponse handleLogin(User user, boolean isNewUser) {
        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessTokenWithAccountEntity(user);
        String refreshToken = jwtTokenProvider.createRefreshTokenWithAccountEntity(user);

        // refresh 토큰 저장
        authRepository.saveRefreshToken(
                user.getId(),
                refreshToken,
                JwtTokenProvider.REFRESH_TOKEN_VALIDITY
        );

        // Jwt 및 로그인 정보 전달
        return GoogleLoginResponse.builder()
                .accountId(user.getId())
                .userRole(user.getRole().getAuthority())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .build();
    }

    public GoogleUserInfo getUserInfo(String accessToken) {
        return webClient.get()
                .uri(googleUserInfoUrl)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
    }
}
