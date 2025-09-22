package knu.fest.knu.fest.global.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.Getter;

@Getter
@Schema(description = "예외 응답 DTO")
public sealed class ExceptionDto permits ArgumentNotValidExceptionDto {
    @JsonProperty("code")
    @Schema(description = "예외 코드", example = "40010")
    @NotNull(message = "예외 코드는 필수입니다.")
    private final Integer code;

    @JsonProperty("message")
    @Schema(description = "예외 메시지", example = "Bad Request")
    @NotNull(message = "예외 메시지는 필수입니다.")
    private final String message;

    public ExceptionDto(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ExceptionDto(String message, ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = message;
    }

    public static ExceptionDto of(ErrorCode errorCode) {
        return new ExceptionDto(errorCode);
    }

    public static ExceptionDto of(String message, ErrorCode errorCode) {
        return new ExceptionDto(message, errorCode);
    }
}
