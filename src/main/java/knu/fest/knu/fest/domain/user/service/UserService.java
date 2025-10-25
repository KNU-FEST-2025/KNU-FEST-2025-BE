package knu.fest.knu.fest.domain.user.service;

import knu.fest.knu.fest.domain.user.controller.dto.UserInfoResponse;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));


        return UserInfoResponse.of(user);
    }

}
