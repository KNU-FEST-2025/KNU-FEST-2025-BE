package knu.fest.knu.fest.domain.Like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import knu.fest.knu.fest.domain.Like.controller.dto.LikeRequest;
import knu.fest.knu.fest.domain.Like.controller.dto.LikeResponse;
import knu.fest.knu.fest.domain.Like.service.LikeService;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/like")
@RequiredArgsConstructor
@Tag(name = "Like API", description = "좋아요 생성/삭제 API")
public class LikeController {
    private final LikeService likeService;

    @Operation(
            summary = "좋아요 생성",
            description = """
            새로운 좋아요를 생성합니다.  
            - 유저ID와 부스ID를 조회해, 중복되지 않으면 생성합니다. 
            - 좋아요가 성공적으로 생성된다면, booth 테이블의 likenum을 1 증가시킵니다.  
            """
    )
    @PostMapping
    public ResponseDto<LikeResponse> create(
            @Valid @RequestBody LikeRequest request
    ) {
        LikeResponse response = likeService.create(request);
        return ResponseDto.created(response);
    }

    @DeleteMapping
    public ResponseDto<LikeResponse> update(
            @Valid @RequestBody LikeRequest request
    ){
        likeService.delete(request);
        return ResponseDto.ok(null);
    }
}
