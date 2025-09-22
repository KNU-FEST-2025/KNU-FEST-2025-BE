package knu.fest.knu.fest.domain.auth.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import knu.fest.knu.fest.domain.auth.dto.kakao.KakaoUserInfo;

public record GoogleUserInfo(
        @JsonProperty("id") String id,
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("picture") String picture
) {
}
