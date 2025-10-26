package knu.fest.knu.fest.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import knu.fest.knu.fest.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final long ACCESS_TOKEN_VALIDITY = 24*60*60*1000L; // 24시간
    public static final long REFRESH_TOKEN_VALIDITY = 30L * 24 * 60 * 60 * 1000; // 30일 (한 달)
    
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String USER_ID_KEY = "userId";

    public String createAccessToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return createToken(
            customUserDetails.getUserId(),
            customUserDetails.getAuthorities(),
            ACCESS_TOKEN_VALIDITY
        );
    }

    public String createAccessTokenWithRefreshTokenInfo(
        Long accountId,
        String authority
    ) {
        return createToken(
            accountId,
            Collections.singletonList(
                new SimpleGrantedAuthority(authority)
            ),
            ACCESS_TOKEN_VALIDITY
        );
    }

    public String createAccessTokenWithAccountEntity(User account) {
        return createToken(
            account.getId(),
            Collections.singletonList(
                new SimpleGrantedAuthority( account.getRole().getAuthority())
            ),
            ACCESS_TOKEN_VALIDITY
        );
    }

    public String createRefreshToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return createToken(
            customUserDetails.getUserId(),
            customUserDetails.getAuthorities(),
            REFRESH_TOKEN_VALIDITY
        );
    }

    public String createRefreshTokenWithAccountEntity(User account) {
        return createToken(
            account.getId(),
            Collections.singletonList(
                new SimpleGrantedAuthority(account.getRole().getAuthority())
            ),
            REFRESH_TOKEN_VALIDITY
        );
    }

    private String createToken(
        Long accountId,
        Collection<? extends GrantedAuthority> authorities,
        long validityInMilliseconds
    ) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
            .setIssuedAt(now)
            .setExpiration(validity)
            .claim(USER_ID_KEY, accountId)
            .claim(AUTHORIZATION_KEY, authorities.iterator().next().getAuthority())
            .signWith(key)
            .compact();
    }

    public Claims getClaimsFromToken(String token) throws JwtException {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            throw new CommonException(ErrorCode.TOKEN_EXPIRED);
        } catch (IllegalArgumentException e) {
            throw new CommonException(ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INVALID_JWT_TOKEN_FORMAT);
        }
    }

    // jwt의 claims로부터 인증 완료된 객체 반환(컨트롤러에서 사용)
    public Authentication getAuthenticationFromClaims(Claims claims) {
        Long accountId = claims.get(USER_ID_KEY,Long.class);
        String authority = claims.get(AUTHORIZATION_KEY, String.class);
        UserDetails userDetails = CustomUserDetails.builder()
            .userId(accountId) // accountId만 이용
            .email(null)
            .password(null)
            .authorities(
                Collections.singletonList(
                    new SimpleGrantedAuthority(authority)
                )
            )
            .build();

        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null, // pw 쓸 일 없음
            userDetails.getAuthorities()
        );
    }
}
