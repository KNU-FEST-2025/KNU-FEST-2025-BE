package knu.fest.knu.fest.domain.comment.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import knu.fest.knu.fest.domain.comment.entity.Comment;

public record CommentResponse(
        @Schema(description = "댓글의 ID", example = "1")
        Long id,

        @Schema(description = "댓글을 단 부스 ID", example = "11")
        Long boothId,

        @Schema(description = "댓글 내용", example = "제육볶음 맛있어요!")
        String content

) {
        public static CommentResponse from(Comment comment) {
                return new CommentResponse(
                        comment.getId(),
                        comment.getBooth().getId(),
                        comment.getContent()
                );
        }
}
