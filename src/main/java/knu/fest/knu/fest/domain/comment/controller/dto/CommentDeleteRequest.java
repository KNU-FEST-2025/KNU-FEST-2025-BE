package knu.fest.knu.fest.domain.comment.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CommentDeleteRequest(
        @Schema(description = "삭제하려는 댓글 Id", example = "11")
        @NotNull
        Long commentId
) {
}
