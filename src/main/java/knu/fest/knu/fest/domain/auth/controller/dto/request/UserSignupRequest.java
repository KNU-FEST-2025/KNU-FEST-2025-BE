package knu.fest.knu.fest.domain.auth.controller.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserSignupRequest(
        @NotBlank(message = "id는 필수 항목입니다")
        @Schema(description = "관리자id")
        String id,

        @NotBlank(message = "비밀번호는 필수 항목입니다")
        @Schema(description = "관리자의 비밀번호", example = "Password123!")
        String password,

        @NotBlank(message = "사용자 닉네임은 필수 항목입니다.")
        @Schema(description = "사용자 닉네임")
        String nickname,

        @NotBlank(message = "권한은 필수 항목입니다.")
        @Schema(description = "관리자 권한")
        String role
) {
}
