package knu.fest.knu.fest.domain.booth.controller.dto;

import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentResponse;

import java.util.List;

public record BoothCreateResponse(
        Long id,
        String name,
        String description,
        Integer boothNumber,
        List<String> imagePath,
        Double longitude,
        Double latitude
) {
    public static BoothCreateResponse from(Booth booth) {
        return new BoothCreateResponse(
                booth.getId(),
                booth.getName(),
                booth.getDescription(),
                booth.getBoothNumber(),
                booth.getImagePath(),
                booth.getLongitude(),
                booth.getLatitude()
        );
    }
}
