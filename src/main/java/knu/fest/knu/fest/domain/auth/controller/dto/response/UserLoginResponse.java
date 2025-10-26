package knu.fest.knu.fest.domain.auth.controller.dto.response;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record UserLoginResponse(
    @NonNull Long userId,
    @NonNull String nickname,
    @NonNull String userRole,
    @NonNull String accessToken,
    @NonNull String refreshToken
) {

}
