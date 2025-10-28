package knu.fest.knu.fest.domain.waiting.controller.dto;

public record WaitingUpdateRequest(
        String nickName,
        String phone,
        Long waitingPeopleNum
) {
}
