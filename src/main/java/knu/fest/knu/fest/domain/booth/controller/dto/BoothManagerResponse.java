package knu.fest.knu.fest.domain.booth.controller.dto;

import knu.fest.knu.fest.domain.booth.entity.Booth;

public record BoothManagerResponse(
        Long boothId,
        String boothName
) {
    public static BoothManagerResponse from(Booth booth) {
        return new BoothManagerResponse(
                booth.getId(),
                booth.getName()
        );
    }
}
