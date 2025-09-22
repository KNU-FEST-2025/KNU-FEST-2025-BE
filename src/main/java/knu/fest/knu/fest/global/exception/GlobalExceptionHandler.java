package knu.fest.knu.fest.global.exception;


import jakarta.servlet.http.HttpServletRequest;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static knu.fest.knu.fest.global.exception.ExceptionUtil.*;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    // 개발자가 직접 정의한 예외
    @ExceptionHandler(value = {CommonException.class})
    public ResponseDto<?> handleApiException(CommonException e, HttpServletRequest req) {
        String requestMethod = req.getMethod();
        String requestURI = req.getRequestURI();

        log.error("GlobalExceptionHandler catch CommonException By User(id:{}) When [{}] {} In [{}] At {} : {}",
                getUserName(),
                requestMethod,
                requestURI,
                getMethodName(e),
                getLineNumber(e),
                e.getMessage());

        return ResponseDto.fail(e);
    }

    // 서버, DB 예외
    @ExceptionHandler(value = {Exception.class})
    public ResponseDto<?> handleException(Exception e, HttpServletRequest req) {
        String requestMethod = req.getMethod();
        String requestURI = req.getRequestURI();

        log.error("GlobalExceptionHandler catch {} By User(id:{}) When [{}] {} In [{}] At {} : {}",
                getSimpleName(e),
                getUserName(),
                requestMethod,
                requestURI,
                getMethodName(e),
                getLineNumber(e),
                e.getMessage());

        return ResponseDto.fail(new CommonException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal =  authentication.getPrincipal();
        if(principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else {
            return "None";
        }
    }
}
