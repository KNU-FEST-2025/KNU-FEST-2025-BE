package knu.fest.knu.fest.global.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Getter
public non-sealed class ArgumentNotValidExceptionDto extends ExceptionDto {
    @NotNull(message = "유효하지 않은 필드 목록은 필수입니다.")
    private final Map<String, String> errorFields;

    public ArgumentNotValidExceptionDto(final MethodArgumentNotValidException methodArgumentNotValidException) {
        super(ErrorCode.INVALID_ARGUMENT);

        this.errorFields = new HashMap<>();
        methodArgumentNotValidException.getBindingResult()
                .getAllErrors().forEach(e -> this.errorFields.put(toSnakeCase(((FieldError) e).getField()), e.getDefaultMessage()));
    }

    public ArgumentNotValidExceptionDto(final ConstraintViolationException constraintViolationException) {
        super(ErrorCode.INVALID_ARGUMENT);

        this.errorFields = new HashMap<>();

        for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
            errorFields.put(toSnakeCase(constraintViolation.getPropertyPath().toString()), constraintViolation.getMessage());
        }
    }

    private String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
