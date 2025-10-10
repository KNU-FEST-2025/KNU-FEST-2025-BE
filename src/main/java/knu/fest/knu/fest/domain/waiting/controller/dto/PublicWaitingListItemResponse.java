package knu.fest.knu.fest.domain.waiting.controller.dto;

import knu.fest.knu.fest.domain.waiting.entity.WaitingStatus;

public record PublicWaitingListItemResponse(
        String nickName,
        WaitingStatus status,
        int waitingNumber
) {
    public static PublicWaitingListItemResponse of(
            String nickName,
            WaitingStatus status,
            int waitingNumber
    ) {
        return new PublicWaitingListItemResponse(
                nickName,
                status,
                waitingNumber);
    }
}
