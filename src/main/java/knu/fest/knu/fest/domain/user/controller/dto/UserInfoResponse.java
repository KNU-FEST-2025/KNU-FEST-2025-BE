package knu.fest.knu.fest.domain.user.controller.dto;


import knu.fest.knu.fest.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserInfoResponse(
        Long userId,
        String userRole
) {
    public static UserInfoResponse of(User user) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .userRole(user.getRole().toString())
                .build();
    }
}
