package knu.fest.knu.fest.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoLoginRequest(
        @JsonProperty("code") String code
) {
}
