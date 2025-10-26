package knu.fest.knu.fest.domain.booth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import knu.fest.knu.fest.domain.booth.entity.Booth;

import java.util.List;

public record BoothCreateRequest(
        @NotBlank @Size(max = 120)
        String name,
        String description,
        @NotNull
        Integer boothNumber,
        List<String> imagePath
) {

    public Booth toEntity() {
        return Booth.builder()
                .name(name)
                .description(description)
                .boothNumber(boothNumber)
                .build();
    }
}
