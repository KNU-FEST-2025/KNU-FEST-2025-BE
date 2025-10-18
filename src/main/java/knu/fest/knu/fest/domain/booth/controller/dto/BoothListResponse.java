package knu.fest.knu.fest.domain.booth.controller.dto;

import knu.fest.knu.fest.domain.booth.entity.Booth;

public record BoothListResponse(
        Long id,
        String name,
        Integer boothNumber,
        Long waitingCount,
        Long likeCount
) {
    public static BoothListResponse of(Booth booth) {
        return new BoothListResponse(
                booth.getId(),
                booth.getName(),
                booth.getBoothNumber(),
                booth.getWaitingCount(),
                booth.getLikeCount()
        );
    }
}
