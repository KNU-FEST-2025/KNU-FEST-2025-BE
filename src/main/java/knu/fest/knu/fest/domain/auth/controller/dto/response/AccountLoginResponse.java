package knu.fest.knu.fest.domain.auth.controller.dto.response;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record AccountLoginResponse(
    @NonNull Long accountId,
    @NonNull String userRole,
    @NonNull String accessToken,
    @NonNull String refreshToken
) {

}
