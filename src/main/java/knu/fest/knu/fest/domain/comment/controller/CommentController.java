package knu.fest.knu.fest.domain.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentCreateRequest;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentDeleteRequest;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentResponse;
import knu.fest.knu.fest.domain.comment.service.CommentService;
import knu.fest.knu.fest.global.annotation.UserId;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/booth/comment")
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "댓글 생성/삭제 API")
public class CommentController {
    private final CommentService commentService;

    @Operation(
            summary = "댓글 생성",
            description = """
            댓글을 생성합니다.
            - 유저ID와 부스 ID를 조회해, 댓글을 생성합니다.
            - 정상 생성되었다면, 생성정보를 반환합니다. 
            """
    )
    @PostMapping
    public ResponseDto<CommentResponse> createComment(
            @UserId Long userId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = commentService.create(userId, request);
        return ResponseDto.created(response);
    }

    @Operation(
            summary = "댓글 삭제",
            description = """
            댓글을 삭제합니다.  
            - 유저ID와 부스ID를 조회해 존재하면 삭제합니다. 
            - 댓글이 성공적으로 삭제된다면, 204 noContent를 반환합니다.
            """
    )
    @DeleteMapping("/{boothId}")
    public ResponseDto<Void> deleteComment(
            @UserId Long userId,
            @Valid @RequestBody CommentDeleteRequest request
    ) {
        commentService.delete(userId, request);
        return ResponseDto.noContent();
    }
}
