package knu.fest.knu.fest.domain.booth.repository;

import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.entity.BoothManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoothManagerRepository extends JpaRepository<BoothManager, Long> {
    boolean existsByBoothIdAndUserId(Long boothId, Long userId);

    @Query("SELECT bm.booth FROM BoothManager bm WHERE bm.user.id = :userId")
    Optional<Booth>  findBoothByUserId(@Param("userId") Long userId);

}
