package knu.fest.knu.fest.domain.auth.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record KakaoLoginResponse(
    @NonNull Long userId,
    @NonNull String nickname,
    @NonNull String userRole,
    @NonNull String accessToken,
    @NonNull String refreshToken,
    @Schema(description = "신규 유저 여부")
    boolean isNewUser
) {

}