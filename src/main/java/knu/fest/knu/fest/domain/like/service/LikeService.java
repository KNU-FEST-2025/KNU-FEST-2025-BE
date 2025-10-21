package knu.fest.knu.fest.domain.like.service;

import knu.fest.knu.fest.domain.like.controller.dto.LikeRequest;
import knu.fest.knu.fest.domain.like.controller.dto.LikeResponse;
import knu.fest.knu.fest.domain.like.entity.Like;
import knu.fest.knu.fest.domain.like.repository.LikeRepository;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final BoothRepository boothRepository;

    /**
     * 좋아요 등록
     * @param request LikeRequest DTO
     */
    public LikeResponse create(Long userId, LikeRequest request) {

        boolean exists = likeRepository.existsByUserIdAndBoothId(userId, request.boothId());
        if (exists) {
            throw new IllegalStateException("이미 좋아요를 누른 부스입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // row-level lock 조회
        Booth booth = boothRepository.findByIdForUpdate(request.boothId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부스입니다."));

        Like like = Like.builder()
                .user(user)
                .booth(booth)
                .build();

        likeRepository.save(like);

        // boothCount + 1
        booth.addLike();
        boothRepository.save(booth);

        return new LikeResponse(like.getId(), like.getBooth().getId(), like.getBooth().getLikeCount());
    }

    public void delete(Long userId, LikeRequest request) {
        Like like = likeRepository.findByUserIdAndBoothId(userId, request.boothId())
                .orElseThrow(() -> new IllegalStateException("좋아요가 존재하지 않습니다."));

        // row-level lock 조회.
        Booth booth = boothRepository.findByIdForUpdate(like.getBooth().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부스입니다."));
        likeRepository.delete(like);

        booth.removeLike();
        boothRepository.save(booth);
    }
}
