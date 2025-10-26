package knu.fest.knu.fest.domain.auth.controller.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfo(
        @JsonProperty("id") String id,
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("picture") String picture
) {
}
