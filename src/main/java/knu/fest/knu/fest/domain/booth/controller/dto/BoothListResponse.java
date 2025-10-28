package knu.fest.knu.fest.domain.booth.controller.dto;

import knu.fest.knu.fest.domain.booth.entity.Booth;

public record BoothListResponse(
        Long id,
        String name,
        Integer boothNumber,
        Long waitingCount,
        Long likeCount,
        boolean likedByMe, // 로그인 사용자가 눌렀는지 여부,
        Double longitude,
        Double latitude
) {
    public static BoothListResponse of(Booth booth, boolean likedByMe) {
        return new BoothListResponse(
                booth.getId(),
                booth.getName(),
                booth.getBoothNumber(),
                booth.getWaitingCount(),
                booth.getLikeCount(),
                likedByMe,
                booth.getLongitude(),
                booth.getLatitude()
        );
    }
}
