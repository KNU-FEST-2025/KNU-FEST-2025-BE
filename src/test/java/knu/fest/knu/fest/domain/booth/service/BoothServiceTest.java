package knu.fest.knu.fest.domain.booth.service;

import knu.fest.knu.fest.domain.booth.controller.dto.BoothListResponse;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.like.entity.Like;
import knu.fest.knu.fest.domain.like.repository.LikeRepository;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.entity.UserRole;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BoothServiceTest {

    @Autowired
    private BoothService boothService;

    @Autowired
    private BoothRepository boothRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    private User user;
    private Booth booth1;
    private Booth booth2;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("tester")
                .role(UserRole.USER)
                .build());

        booth1 = boothRepository.save(Booth.builder()
                .name("Booth A")
                .boothNumber(101)
                .build());

        booth2 = boothRepository.save(Booth.builder()
                .name("Booth B")
                .boothNumber(102)
                .build());

        // user가 booth2에 좋아요 누른 상태로 초기 설정
        Like like = Like.builder()
                .user(user)
                .booth(booth2)
                .build();
        likeRepository.save(like); // save(user, booth) 형태로 좋아요 저장 메서드 가정
    }

    @Test
    @DisplayName("부스 전체 조회 테스트 - 로그인 사용자")
    void testGetAllBoothsForLoggedInUser() {
        Long userId = user.getId();

        List<BoothListResponse> responseList = boothService.getAllBooths(userId);

        assertThat(responseList).hasSize(2);

        BoothListResponse b1 = responseList.get(0);
        assertThat(b1.id()).isEqualTo(booth1.getId());
        assertThat(b1.name()).isEqualTo("Booth A");
        assertThat(b1.likedByMe()).isFalse();

        BoothListResponse b2 = responseList.get(1);
        assertThat(b2.id()).isEqualTo(booth2.getId());
        assertThat(b2.name()).isEqualTo("Booth B");
        assertThat(b2.likedByMe()).isTrue(); // user가 좋아요 누른 상태
    }

    @Test
    @DisplayName("부스 전체 조회 테스트 - 비로그인 사용자")
    void testGetAllBoothsForAnonymousUser() {
        Long userId = null; // 비로그인

        List<BoothListResponse> responseList = boothService.getAllBooths(userId);

        assertThat(responseList).hasSize(2);

        responseList.forEach(booth -> {
            assertThat(booth.likedByMe()).isFalse();
        });

    }
}