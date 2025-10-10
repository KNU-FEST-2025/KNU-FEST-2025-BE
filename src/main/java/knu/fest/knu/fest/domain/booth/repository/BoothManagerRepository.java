package knu.fest.knu.fest.domain.booth.repository;

import knu.fest.knu.fest.domain.booth.entity.BoothManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoothManagerRepository extends JpaRepository<BoothManager, Long> {
    boolean existsByBoothIdAndUserId(Long boothId, Long userId);
}
