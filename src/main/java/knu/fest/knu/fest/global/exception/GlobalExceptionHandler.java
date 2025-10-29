package knu.fest.knu.fest.global.exception;


import jakarta.servlet.http.HttpServletRequest;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static knu.fest.knu.fest.global.exception.ExceptionUtil.*;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @Value("${webhook.goole}")
    private static final String webhook_url;

    // 개발자가 정의한 예외
    @ExceptionHandler(CommonException.class)
    public ResponseDto<?> handleApiException(CommonException e, HttpServletRequest req) {
        String user = getUserName();
        String method = req.getMethod();
        String uri = req.getRequestURI();

        log.error("GlobalExceptionHandler catch CommonException By User(id:{}) When [{}] {} In [{}] At {} : {}",
                user, method, uri, getMethodName(e), getLineNumber(e), e.getMessage());

        sendErrorToGoogleSheet(user, method, uri, getSimpleName(e), e.getMessage(), getStackTraceAsString(e));

        return ResponseDto.fail(e);
    }

    // 서버, DB 예외
    @ExceptionHandler(Exception.class)
    public ResponseDto<?> handleException(Exception e, HttpServletRequest req) {
        String user = getUserName();
        String method = req.getMethod();
        String uri = req.getRequestURI();

        log.error("GlobalExceptionHandler catch {} By User(id:{}) When [{}] {} In [{}] At {} : {}",
                getSimpleName(e), user, method, uri, getMethodName(e), getLineNumber(e), e.getMessage());

        sendErrorToGoogleSheet(user, method, uri, getSimpleName(e), e.getMessage(), getStackTraceAsString(e));

        return ResponseDto.fail(new CommonException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    /**
     * Google Sheet로 에러 로그 전송
     */
    private void sendErrorToGoogleSheet(String user, String method, String uri,
                                        String exception, String message, String stackTrace) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> body = new HashMap<>();
            body.put("user", user);
            body.put("method", method);
            body.put("uri", uri);
            body.put("exception", exception);
            body.put("message", message);
            body.put("stackTrace", stackTrace);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(webhook_url, entity, String.class);

        } catch (Exception ex) {
            log.error("Failed to send error log to Google Sheet: {}", ex.getMessage());
        }
    }

    /**
     * 예외의 StackTrace를 문자열로 변환
     */
    private String getStackTraceAsString(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String full = sw.toString();
        int limit = Math.min(full.length(), 800);
        return full.substring(0, limit);
    }

    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return "Anonymous";

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return "None";
    }
}