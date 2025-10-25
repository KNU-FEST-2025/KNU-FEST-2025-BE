package knu.fest.knu.fest.domain.comment.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
        @Schema(description = "댓글 내용", example = "제육볶음 맛있어요!")
        String content
) {
}
