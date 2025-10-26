package knu.fest.knu.fest.global.interceptor.pre;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knu.fest.knu.fest.global.constant.Constants;
import knu.fest.knu.fest.global.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return true; // 로그인 안한 사용자
        }

        Object principal = authentication.getPrincipal();
        String userId = null;

        if (principal instanceof CustomUserDetails customUserDetails) {
            userId = customUserDetails.getUsername();
        } else if (principal instanceof String strPrincipal) {
            userId = null;
        }

        if (userId != null) {
            request.setAttribute(Constants.USER_ID_ATTRIBUTE_NAME, userId);
            System.out.println("User ID set from interceptor: " + userId);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
