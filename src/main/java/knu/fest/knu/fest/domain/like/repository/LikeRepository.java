package knu.fest.knu.fest.domain.like.repository;

import knu.fest.knu.fest.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndBoothId(Long userId, Long boothId);

    Optional<Like> findByUserIdAndBoothId(Long userId, Long boothId);
}
