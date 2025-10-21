package knu.fest.knu.fest.domain.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import knu.fest.knu.fest.domain.notice.dtos.request.CreateNoticeRequestDto;
import knu.fest.knu.fest.domain.notice.dtos.response.NoticeDto;
import knu.fest.knu.fest.domain.notice.dtos.response.ViewNoticeResponseDto;
import knu.fest.knu.fest.domain.notice.service.NoticeService;
import knu.fest.knu.fest.global.annotation.UserId;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.AllArgsConstructor;
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

    @Operation(
            description = "전체 게시물 조회하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시물 리스트"),
            }
    )
    @GetMapping("/user")
    public ResponseDto<ViewNoticeResponseDto> viewAll() {
        ViewNoticeResponseDto response = noticeService.viewAll();

        return ResponseDto.ok(response);
    }

    @Operation(
            description = "게시물 상세 정보 조회.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시물 상세정보"),
                    @ApiResponse(responseCode = "404", description = "게시물 존재하지 않음")
            }
    )
    @GetMapping("/user/{id}")
    public ResponseDto<NoticeDto> getNotice (@PathVariable Long id) {
        NoticeDto response = noticeService.getNotice(id);

        return ResponseDto.ok(response);
    }

    @Operation(
            description = "게시물 삭제",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시물 삭제 완료"),
                    @ApiResponse(responseCode = "404", description = "게시물 존재하지 않음")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseDto<String> delete(@PathVariable Long id) {
        String response = noticeService.delete(id);

        return ResponseDto.ok(response);
    }

    @Operation(
            description = "게시물 수정",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시물 수정 완료"),
                    @ApiResponse(responseCode = "404", description = "게시물 존재하지 않음")
            }
    )
    @PutMapping ("/{id}")
    public ResponseDto<String> update(@PathVariable Long id, @RequestBody CreateNoticeRequestDto requestDto) {
        String response = noticeService.update(id, requestDto);

        return ResponseDto.ok(response);
    }


}
