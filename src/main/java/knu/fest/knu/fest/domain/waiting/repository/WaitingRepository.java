package knu.fest.knu.fest.domain.waiting.repository;

import knu.fest.knu.fest.domain.waiting.entity.Waiting;
import knu.fest.knu.fest.domain.waiting.entity.WaitingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {
    // 공개 닉네임 리스트 DB 복구/백업/관리용
    List<Waiting> findAllByBoothIdAndStatusOrderByIdAsc(Long boothId, WaitingStatus status);

    // 사용자: 부스 + 웨이팅pk 로 조회 (보안/정합성 위해 부스도 같이 확인)
    Optional<Waiting> findByIdAndBoothId(Long id, Long boothId);

    List<Waiting> findAllByIdIn(Collection<Long> ids);

    // DONE/CANCELLED용 조회
    List<Waiting> findAllByBoothIdAndStatusOrderByCreatedAtAsc(Long boothId, WaitingStatus status);

}
