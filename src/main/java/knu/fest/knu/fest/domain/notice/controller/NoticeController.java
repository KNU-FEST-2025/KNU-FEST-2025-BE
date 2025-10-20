package knu.fest.knu.fest.domain.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import knu.fest.knu.fest.domain.notice.dtos.request.CreateNoticeRequestDto;
import knu.fest.knu.fest.domain.notice.service.NoticeService;
import knu.fest.knu.fest.global.annotation.UserId;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(
            summary = "게시물 등록",
            description = "게시물 등록하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시물 생성 완료: {NoticeId}"),
            }
    )
    @PostMapping
    public ResponseDto<String> create(
            //@Parameter(hidden = true) @UserId Long userId,
            @RequestParam Long userId,
            @RequestBody CreateNoticeRequestDto request) {

        String createdId = noticeService.create(userId, request);

        return ResponseDto.ok("게시물 생성 완료: " + createdId);
    }

}
