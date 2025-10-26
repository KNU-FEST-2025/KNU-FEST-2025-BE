package knu.fest.knu.fest.domain.like.repository;

import knu.fest.knu.fest.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndBoothId(Long userId, Long boothId);

    Optional<Like> findByUserIdAndBoothId(Long userId, Long boothId);

    @Query("SELECT l.booth.id FROM Like l WHERE l.user.id = :userId")
    List<Long> findAllBoothIdsByUserId(@Param("userId") Long userId);
}
