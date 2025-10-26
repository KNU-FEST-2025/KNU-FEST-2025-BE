package knu.fest.knu.fest.global.interceptor.pre;

import knu.fest.knu.fest.global.annotation.UserId;
import knu.fest.knu.fest.global.constant.Constants;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Long.class)
                && parameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        final String userIdObj = (String) webRequest.getAttribute(
                Constants.USER_ID_ATTRIBUTE_NAME,
                WebRequest.SCOPE_REQUEST
        );

        if (userIdObj == null) {
            return null;
        }

        try {
            return Long.parseLong(userIdObj);
        } catch (NumberFormatException e) {
            // 혹시 잘못된 값이 들어온 경우도 안전하게 처리
            throw new CommonException(ErrorCode.INVALID_TOKEN);
        }
    }

}
