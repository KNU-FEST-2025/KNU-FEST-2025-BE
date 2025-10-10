package knu.fest.knu.fest.domain.waiting.controller.dto;

import knu.fest.knu.fest.domain.waiting.entity.Waiting;
import knu.fest.knu.fest.domain.waiting.entity.WaitingStatus;

public record WaitingStatusResponse(
        Long waitingId,
        String nickName,
        String phone,
        String status
) {
    public static WaitingStatusResponse of(Waiting waiting) {
        return new WaitingStatusResponse(
                waiting.getId(),
                waiting.getNickName(),
                waiting.getPhone(),
                waiting.getStatus().name()
        );
    }

    public static WaitingStatusResponse of(Long waitingId, String nickName, String phone, WaitingStatus status) {
        return new WaitingStatusResponse(waitingId, nickName, phone, status.name());
    }
}
