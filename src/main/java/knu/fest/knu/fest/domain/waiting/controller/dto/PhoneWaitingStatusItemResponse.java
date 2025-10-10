package knu.fest.knu.fest.domain.waiting.controller.dto;

import knu.fest.knu.fest.domain.waiting.entity.WaitingStatus;

public record PhoneWaitingStatusItemResponse(
        Long boothId,
        String boothName,
        String nickName,
        WaitingStatus status,
        Integer WaitingNumber
) {
}
