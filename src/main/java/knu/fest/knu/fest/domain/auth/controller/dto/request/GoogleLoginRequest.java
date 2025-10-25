package knu.fest.knu.fest.domain.auth.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleLoginRequest(
        @JsonProperty("code") String code
) {
}
