package knu.fest.knu.fest.global.security.handler;


import jakarta.servlet.http.HttpServletResponse;
import knu.fest.knu.fest.global.common.ExceptionDto;
import knu.fest.knu.fest.global.exception.ErrorCode;
import net.minidev.json.JSONValue;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AbstractAuthenticationFailureHandler {
    protected void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode
    ) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("data", null);
        result.put("error", ExceptionDto.of(errorCode));

        response.getWriter().write(JSONValue.toJSONString(result));
    }

    protected void setErrorResponse(
            HttpServletResponse response,
            String message,
            ErrorCode errorCode
    ) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("data", null);
        result.put("error", ExceptionDto.of(message, errorCode));

        response.getWriter().write(JSONValue.toJSONString(result));
    }
}
