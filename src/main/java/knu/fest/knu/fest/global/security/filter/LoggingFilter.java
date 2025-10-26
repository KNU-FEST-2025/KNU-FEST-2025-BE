package knu.fest.knu.fest.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger("knu.fest.user.logger");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String clientIp = getClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            logger.error("(IP={}) : [{}] {} -> Exception:{} | {}ms",
                    clientIp, method, uri, e.getClass().getSimpleName(), duration, e);
            throw e;
        }

        long duration = System.currentTimeMillis() - startTime;
        int status = response.getStatus();

        logger.info("(IP={}) : [{}] {} -> Status:{} | {}ms",
                clientIp, method, uri, status, duration);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }
}
