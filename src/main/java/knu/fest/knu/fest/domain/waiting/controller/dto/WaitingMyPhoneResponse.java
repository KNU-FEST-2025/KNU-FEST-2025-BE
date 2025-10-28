package knu.fest.knu.fest.domain.waiting.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WaitingMyPhoneResponse {
    private Long boothId;
    private String boothName;
    private String imagePath;
    private Integer waitingNum;
}
