package knu.fest.knu.fest.global.constant;

import java.util.List;

public class Constants {
    public static String USER_ID_ATTRIBUTE_NAME = "USER_ID";
    public static String USER_ID_CLAIM_NAME = "uid";
    public static String USER_ROLE_CLAIM_NAME = "rol";

    public static List<String> ADMIN_AUTH_URLS = List.of(
            "/api/v1/admin/notice/**",
            "/api/v1/applicant"
    );

    public static List<String> BOOTH_ADMIN_AUTH_URLS = List.of(
            "/api/v1/admin/lost-item",
            "/api/v1/admin/lost-item/**",
            "/api/v1/admin/waiting",
            "/api/v1/admin/waiting/**"
    );

    public static List<String> USER_AUTH_URLS = List.of(
            "/api/v1/booth/{boothId}/comment",
            "/api/v1/booth/{boothId}/comment/**",
            "/api/v1/like",
            "/api/v1/user/info"
    );

    public static List<String> NO_NEED_OR_USER_AUTH_URLS = List.of(
            "/api/v1/booth"
    );


    public static List<String> NO_NEED_AUTH_URLS = List.of(
            "/api/v1/auth/signup",
            "/api/v1/auth/login",
            "/api/v1/auth/token/refresh",
            "/api/v1/oauth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",

            "/api/v1/booth/*",
            "/api/v1/lost-item/user/**",
            "/api/v1/notice/**",
            "/api/v1/files/**",
            "/api/v1/waiting/{boothId}/public",
            "/api/v1/subscribe/all",
            "/api/v1/like/subscribe/**",
            "/api/v1/waiting/subscribe/**",
            "/api/v1/booth/subscribe/**"
    );
}
