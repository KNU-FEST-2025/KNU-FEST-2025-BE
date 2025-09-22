package knu.fest.knu.fest.global.exception;

import io.jsonwebtoken.JwtException;
import lombok.Getter;

@Getter
public class JsonWebTokenException extends JwtException {
    private final ErrorCode errorCode;

    public JsonWebTokenException(ErrorCode errorCode) {
        super(errorCode.getMessage());

        this.errorCode = errorCode;
    }
}
