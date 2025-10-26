package knu.fest.knu.fest.domain.booth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BoothUpdateRequest(
        @NotBlank @Size(max = 120)
        String name,
        String description,
        List<String> imagePath
) {

}
