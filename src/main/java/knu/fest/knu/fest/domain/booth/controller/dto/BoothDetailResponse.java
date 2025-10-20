package knu.fest.knu.fest.domain.booth.controller.dto;

import knu.fest.knu.fest.domain.booth.entity.Booth;

public record BoothDetailResponse(
        Long id,
        String name,
        String description,
        Integer boothNumber,
        Long waitingCount,
        Long likeCount
        // TODO: images, .. 등 추가 예정
) {
    public static BoothDetailResponse of(Booth booth) {
        return new BoothDetailResponse(
                booth.getId(),
                booth.getName(),
                booth.getDescription(),
                booth.getBoothNumber(),
                booth.getWaitingCount(),
                booth.getLikeCount()
        );
    }
}
