package knu.fest.knu.fest.domain.Like.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import knu.fest.knu.fest.domain.Like.entity.Like;

public record LikeRequest(
        @Schema(description = "좋아요를 누를 부스 ID", example = "11")
        @NotNull
        Long boothId
) {
}
