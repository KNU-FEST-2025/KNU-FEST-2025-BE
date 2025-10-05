package knu.fest.knu.fest.domain.Like.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LikeResponse(
        @Schema(description = "좋아요 ID", example = "10")
        Long likeId,

        @Schema(description = "부스 ID", example = "1")
        Long boothId,

        @Schema(description = "좋아요를 누른 사용자 ID", example = "1001")
        Long userId,

        @Schema(description = "해당 부스의 현재 좋아요 수", example = "25")
        Long likeCount
) {
}
