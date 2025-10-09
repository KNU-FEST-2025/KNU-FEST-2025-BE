package knu.fest.knu.fest.domain.Like.service;

import knu.fest.knu.fest.domain.Like.controller.dto.LikeRequest;
import knu.fest.knu.fest.domain.Like.controller.dto.LikeResponse;
import knu.fest.knu.fest.domain.Like.repository.LikeRepository;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test") // application-test.yml 사용
class LikeServiceIntegrationTest {

    @Autowired
    private LikeServiceImpl likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoothRepository boothRepository;

    private User user;
    private Booth booth;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("tester")
                .role(UserRole.USER)
                .build());

        booth = boothRepository.save(Booth.builder()
                .name("테스트 부스")
                .description("테스트용 부스")
                .boothNumber(1)
                .likeCount(0L)
                .build());
    }

    @Test
    @DisplayName("좋아요 생성 테스트 (MySQL)")
    void testCreateLike() {
        LikeRequest request = new LikeRequest(booth.getId());

        LikeResponse response = likeService.create(user.getId(), request);

        // like 정상 생성인지 확인
        assertThat(likeRepository.existsByUserIdAndBoothId(user.getId(), booth.getId())).isTrue();

        // booth의 LikeCount 증가 여부 확인
        Booth updatedBooth = boothRepository.findById(booth.getId()).orElseThrow();
        assertThat(updatedBooth.getLikeCount()).isEqualTo(1);

        // 좋아요 중복생성시, -> 오류
        assertThrows(IllegalStateException.class, () -> likeService.create(user.getId(), request));
    }

    @Test
    @DisplayName("좋아요 삭제 테스트 (MySQL)")
    void testDeleteLike() {
        LikeRequest request = new LikeRequest(booth.getId());

        likeService.create(user.getId(), request);

        likeService.delete(user.getId(), request);

        // 좋아요가 DB에서 삭제되었는지 확인
        assertThat(likeRepository.existsByUserIdAndBoothId(user.getId(), booth.getId())).isFalse();

        // 부스 likeCount 감소 확인
        Booth updatedBooth = boothRepository.findById(booth.getId()).orElseThrow();
        assertThat(updatedBooth.getLikeCount()).isEqualTo(0);

        // 없는 좋아요 삭제 시 예외 발생
        assertThrows(IllegalStateException.class, () -> likeService.delete(user.getId(), request));
    }

    @Test
    @DisplayName("좋아요 생성 트랜잭션 롤백 테스트")
    void testCreateLikeRollback() {
        // 존재하지 않는 boothId를 주어 예외 발생
        LikeRequest faultyRequest = new LikeRequest(9999L); // 실제 DB에 없는 부스
        assertThrows(IllegalArgumentException.class, () -> likeService.create(user.getId(), faultyRequest));

        // 롤백 확인: like 테이블에 아무것도 남지 않았는지 확인
        assertThat(likeRepository.findAll()).isEmpty();

        // 부스 likeCount가 변경되지 않았는지 확인
        Booth updatedBooth = boothRepository.findById(booth.getId()).orElseThrow();
        assertThat(updatedBooth.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("좋아요 삭제 트랜잭션 롤백 테스트")
    void testDeleteLikeRollback() {
        // 먼저 정상적으로 좋아요 생성
        LikeRequest request = new LikeRequest(booth.getId());
        likeService.create(user.getId(), request);

        // delete 시 존재하지 않는 boothId를 줘서 예외 발생
        LikeRequest faultyRequest = new LikeRequest(9999L);
        assertThrows(IllegalStateException.class, () -> likeService.delete(user.getId(), faultyRequest));

        // 롤백 확인: 원래 좋아요는 그대로 존재
        assertThat(likeRepository.existsByUserIdAndBoothId(user.getId(), booth.getId())).isTrue();

        // 부스 likeCount도 그대로
        Booth updatedBooth = boothRepository.findById(booth.getId()).orElseThrow();
        assertThat(updatedBooth.getLikeCount()).isEqualTo(1);
    }


}

