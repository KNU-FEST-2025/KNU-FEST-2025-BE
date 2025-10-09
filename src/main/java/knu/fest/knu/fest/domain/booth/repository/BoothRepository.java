package knu.fest.knu.fest.domain.booth.repository;

import jakarta.persistence.LockModeType;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoothRepository extends JpaRepository<Booth, Long> {
    boolean existsByBoothNumber(Integer boothNumber);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booth b WHERE b.id = :id")
    Optional<Booth> findByIdForUpdate(@Param("id") Long id);

}
