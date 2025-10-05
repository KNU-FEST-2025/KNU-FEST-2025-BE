package knu.fest.knu.fest.domain.lostItem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import knu.fest.knu.fest.domain.lostItem.dtos.request.CreateLostItmeRequestDto;
import knu.fest.knu.fest.domain.lostItem.dtos.response.ViewLostItemResponseDto;
import knu.fest.knu.fest.domain.lostItem.service.LostItemService;
import knu.fest.knu.fest.global.annotation.UserId;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/lost-item")
@RequiredArgsConstructor
public class LostItemController {

    private final LostItemService lostItemService;


    @Operation(
            summary = "분실물 게시물 등록",
            description = "분실물 게시물 등록하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시물 생성 완료: {lostItemId}"),
            }
    )
    @PostMapping
    public ResponseDto<String> create(
            // @Parameter(hidden = true) @UserId Long userId,
            @RequestParam Long userId,
            @RequestBody CreateLostItmeRequestDto request
            ) {
        String createdId = lostItemService.create(userId, request);

        return ResponseDto.ok("게시물 생성 완료: " + createdId);
    }

    @Operation(
            summary = "분실물 게시물 리스트 조회",
            description = "분실물 게시물 조회하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시물 생성 완료: {lostItemId}"),
            }
    )
    @GetMapping
    public ResponseDto<ViewLostItemResponseDto> viewAll () {
        ViewLostItemResponseDto response = lostItemService.viewAll();

        return ResponseDto.ok(response);
    }
}
