package knu.fest.knu.fest.global.constant;

import java.util.List;

public class Constants {
    public static String USER_ID_ATTRIBUTE_NAME = "USER_ID";
    public static String USER_ID_CLAIM_NAME = "uid";
    public static String USER_ROLE_CLAIM_NAME = "rol";

    public static List<String> NO_NEED_AUTH_URLS = List.of(
            "/api/v1/auth/signup",
            "/api/v1/auth/login",
            "/api/v1/auth/token/refresh",
            "/api/v1/oauth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1/lost-item/user/**",
            "/api/v1/notice/**",
            "/api/v1/files/**"
    );
}
