package knu.fest.knu.fest.domain.waiting.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.waiting.entity.Waiting;

public record WaitingRegisterRequest(
        @NotNull Long boothId,
        @NotBlank String nickName,
        @NotBlank String phone
) {
    public Waiting toEntity(Booth booth) {
        return Waiting.builder()
                .booth(booth)
                .nickName(nickName)
                .phone(phone)
                .build();
    }
}
