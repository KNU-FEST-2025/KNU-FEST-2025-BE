package knu.fest.knu.fest.domain.booth.controller.dto;

import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentResponse;

import java.util.List;

public record BoothDetailResponse(
        Long id,
        String name,
        String description,
        Integer boothNumber,
        Long waitingCount,
        Long likeCount,
        List<CommentResponse> comments
        // TODO: images, .. 등 추가 예정
) {
    public static BoothDetailResponse of(Booth booth, List<CommentResponse> comments) {
        return new BoothDetailResponse(
                booth.getId(),
                booth.getName(),
                booth.getDescription(),
                booth.getBoothNumber(),
                booth.getWaitingCount(),
                booth.getLikeCount(),
                comments
        );
    }
}
