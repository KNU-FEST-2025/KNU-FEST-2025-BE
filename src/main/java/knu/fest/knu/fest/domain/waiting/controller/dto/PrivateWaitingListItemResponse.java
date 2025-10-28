package knu.fest.knu.fest.domain.waiting.controller.dto;

import knu.fest.knu.fest.domain.waiting.entity.WaitingStatus;

import java.time.LocalDateTime;

public record PrivateWaitingListItemResponse(
        Long waitingId,
        String nickName,
        String phone,
        LocalDateTime registrationDate,
        WaitingStatus status,
        Long waitingPeopleNum,
        int order
) {

    public static PrivateWaitingListItemResponse of(
            Long waitingId,
            String nickName,
            String phone,
            LocalDateTime registrationDate,
            WaitingStatus status,
            Long waitingPeopleNum,
            int order
    ) {
        return new PrivateWaitingListItemResponse(
                waitingId,
                nickName,
                phone,
                registrationDate,
                status,
                waitingPeopleNum,
                order);
    }
}
