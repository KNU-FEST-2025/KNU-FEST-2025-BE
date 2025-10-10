package knu.fest.knu.fest.domain.booth.service;

import knu.fest.knu.fest.domain.booth.repository.BoothManagerRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import knu.fest.knu.fest.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoothAuthService {
    private final BoothManagerRepository boothManagerRepository;


    public void check(Long boothId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new CommonException(ErrorCode.ACCESS_DENIED);
        var principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails customUserDetails))
            throw new CommonException(ErrorCode.ACCESS_DENIED);
        if (!boothManagerRepository.existsByBoothIdAndUserId(boothId, customUserDetails.getUserId())) {
            throw new CommonException(ErrorCode.ACCESS_DENIED);
        }
    }
}
