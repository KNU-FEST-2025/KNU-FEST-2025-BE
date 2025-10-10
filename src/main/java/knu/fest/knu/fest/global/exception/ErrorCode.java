package knu.fest.knu.fest.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Not Found Error
    NOT_FOUND_END_POINT(40400, HttpStatus.NOT_FOUND, "존재하지 않는 API 엔드포인트입니다."),
    NOT_FOUND_USER(40401, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    NOT_FOUND_POST(40402, HttpStatus.NOT_FOUND, "존재하지 않는 게시물입니다."),
    NOT_FOUND_LIKE(40403, HttpStatus.NOT_FOUND, "존재하지 않는 좋아요입니다."),
    NOT_FOUND_PLACE(40403, HttpStatus.NOT_FOUND, "존재하지 않는 건물입니다."),
    NOT_FOUND_BOOTH(40404, HttpStatus.NOT_FOUND, "존재하지 않는 부스입니다."),
    NOT_FOUND_WAITING(40405, HttpStatus.NOT_FOUND, "존재하지 않는 웨이팅입니다."),


    // Invalid Argument Error
    INVALID_HEADER_ERROR(40000, HttpStatus.BAD_REQUEST, "유효하지 않은 헤더입니다."),
    MISSING_REQUEST_PARAMETER(40000, HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
    INVALID_PARAMETER_FORMAT(40001, HttpStatus.BAD_REQUEST, "요청에 유효하지 않은 인자 형식입니다."),
    INVALID_ARGUMENT(40002, HttpStatus.BAD_REQUEST, "유효하지 않은 인자입니다."),
    ALREADY_HAVE_LIKE(40003, HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다."),
    ALREADY_EXIST_BOOTH_NUMBER(40004, HttpStatus.BAD_REQUEST, "이미 존재하는 부스 번호입니다."),

    // Access Denied Error
    ACCESS_DENIED(40300, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    TOKEN_EXPIRED(40301, HttpStatus.FORBIDDEN, "토큰이 만료되었습니다."),
    INVALID_JWT_TOKEN_FORMAT(40302, HttpStatus.FORBIDDEN, "토큰 형식이 맞지 않습니다. 올바른 토큰 형식 'Authorization' : 'Bearer {token}')"),
    POST_EDIT_FORBIDDEN(40303, HttpStatus.FORBIDDEN, "게시물을 수정할 권한이 없습니다."),

    // Internal Server Error
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.")
    ;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
