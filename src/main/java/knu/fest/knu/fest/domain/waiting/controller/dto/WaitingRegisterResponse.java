package knu.fest.knu.fest.domain.waiting.controller.dto;

public record WaitingRegisterResponse(
        Long waitingId,
        String nickName
) {
    public static WaitingRegisterResponse of(Long waitingId, String nickName) {
        return new WaitingRegisterResponse(waitingId, nickName);
    }
}
