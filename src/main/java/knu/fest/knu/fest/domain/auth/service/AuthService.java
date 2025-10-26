package knu.fest.knu.fest.domain.auth.service;

import io.jsonwebtoken.Claims;
import knu.fest.knu.fest.domain.auth.controller.dto.request.UserLoginRequest;
import knu.fest.knu.fest.domain.auth.controller.dto.request.UserSignupRequest;
import knu.fest.knu.fest.domain.auth.controller.dto.response.UserLoginResponse;
import knu.fest.knu.fest.domain.auth.controller.dto.response.TokenRefreshResponse;
import knu.fest.knu.fest.domain.auth.repository.AuthRepository;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.entity.UserRole;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import knu.fest.knu.fest.global.security.CustomUserDetails;
import knu.fest.knu.fest.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AuthService{

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void signup(UserSignupRequest request) {
        UserRole role = UserRole.valueOf(request.role());
        userRepository.save(
                User.builder()
                        .email(request.id())
                        .password(passwordEncoder.encode(request.password()))
                        .nickname(request.nickname())
                        .role(role)
                        .build()
                );
    }

    public UserLoginResponse login(UserLoginRequest loginRequestDto) {
        // 인증
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequestDto.email(),
                loginRequestDto.password()
            )
        );
        log.info("인증 완료: {}", authentication.getName());

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // Refresh 토큰 저장
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long accountId = customUserDetails.getUserId();
        authRepository.saveRefreshToken(
            accountId,
            refreshToken,
            JwtTokenProvider.REFRESH_TOKEN_VALIDITY
        );

        String userRole = customUserDetails.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
            .orElse("ROLE_USER"); // 권한이 없는 경우 null 반환

        String userNickname = userRepository.findNicknameByUserId(accountId);

        return UserLoginResponse.builder()
            .userId(accountId)
                .nickname(userNickname)
                .userRole(userRole)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public TokenRefreshResponse getNewAccessToken(String refreshToken) {
        // refresh 토큰 유효성 검사
        Claims claims = jwtTokenProvider.getClaimsFromToken(refreshToken); // throws jwtException
        Long accountId = claims.get(JwtTokenProvider.USER_ID_KEY, Long.class);
        String authority = claims.get(JwtTokenProvider.AUTHORIZATION_KEY, String.class);

        // 저장된 Refresh 토큰과 비교
        String savedRefreshToken = authRepository.findRefreshTokenByAccountId(accountId)
            .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_END_POINT));
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new CommonException(ErrorCode.NOT_FOUND_END_POINT);
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessTokenWithRefreshTokenInfo(
            accountId,
            authority
        );

        return TokenRefreshResponse.builder()
            .accessToken(newAccessToken)
            .build();
    }

    public void logout(Long accountId) {
        // Refresh Token 삭제
        authRepository.deleteRefreshToken(accountId);
    }
}