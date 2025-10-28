package knu.fest.knu.fest.domain.waiting.controller.dto;

public record WaitingRegisterResponse(
        Long waitingId,
        String nickName,
        Long waitingPeopleNum
) {
    public static WaitingRegisterResponse of(Long waitingId, String nickName, Long waitingPeopleNum) {
        return new WaitingRegisterResponse(waitingId, nickName, waitingPeopleNum);
    }
}
