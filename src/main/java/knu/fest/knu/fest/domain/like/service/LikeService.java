package knu.fest.knu.fest.domain.like.service;

import knu.fest.knu.fest.domain.booth.service.BoothSseService;
import knu.fest.knu.fest.domain.like.controller.dto.LikeRequest;
import knu.fest.knu.fest.domain.like.controller.dto.LikeResponse;
import knu.fest.knu.fest.domain.like.entity.Like;
import knu.fest.knu.fest.domain.like.repository.LikeRepository;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.like.sse.LikeSseNotifier;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeSseNotifier likeSseNotifier;
    private final LikeCacheService likeCacheService;
    private final UserRepository userRepository;
    private final BoothRepository boothRepository;
    private final BoothSseService boothSseService;

    /**
     * 좋아요 등록
     * @param request LikeRequest DTO
     */
    public LikeResponse create(Long userId, LikeRequest request) {
        System.out.println("userId: " + userId);

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
        boothSseService.sendAllBoothUpdate(booth.getId(), booth.getLikeCount(), booth.getWaitingCount());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                likeCacheService.enqueue(request.boothId(), like.getId());
                likeSseNotifier.notifyCount(request.boothId(), likeCacheService.count(request.boothId()));
            }
        });

        return new LikeResponse(like.getId(), like.getBooth().getId(), like.getBooth().getLikeCount());
    }

    public void delete(Long userId, LikeRequest request) {
        Like like = likeRepository.findByUserIdAndBoothId(userId, request.boothId())
                .orElseThrow(() -> new IllegalStateException("좋아요가 존재하지 않습니다."));

        // row-level lock 조회.
        Booth booth = boothRepository.findByIdForUpdate(like.getBooth().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부스입니다."));
        likeRepository.delete(like);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                likeCacheService.remove(request.boothId(), like.getId());
                likeSseNotifier.notifyCount(request.boothId(), likeCacheService.count(request.boothId()));
            }
        });

        booth.removeLike();
        boothRepository.save(booth);

        boothSseService.sendAllBoothUpdate(booth.getId(), booth.getLikeCount(), booth.getWaitingCount());
    }
}
