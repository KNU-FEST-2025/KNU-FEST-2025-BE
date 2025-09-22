package knu.fest.knu.fest.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import knu.fest.knu.fest.global.annotation.UserId;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    @Operation(
            summary = "UserId 어노테이션 테스트",
            description = "UserId 어노테이션 테스트입니다. @UserId 어노테이션이면 User 의 PK 를 쉽게 조회할 수 있습니다. 제 선물입니다..ㅎㅎ",
            responses = {
                    @ApiResponse(responseCode = "200", description = " UserId 추출 성공 "),
            }
    )
    @GetMapping("/userId-test")
    public ResponseDto<String> signup(
            @Parameter(hidden = true) @UserId Long userId
    ) {
        return ResponseDto.ok("USER_ID : " + userId.toString());
    }
}
