package knu.fest.knu.fest.domain.applicant.repository;

import knu.fest.knu.fest.domain.applicant.entity.Applicant;
import knu.fest.knu.fest.domain.applicant.entity.ApplicantRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    // 학번으로 중복 여부 확인
    Optional<Applicant> findByStudentNum(Long studentNum);

    // 학과와 학번으로 특정 응모자 조회 (삭제용)
    Optional<Applicant> findByDepartmentAndStudentNum(String department, Long studentNum);

    List<Applicant> findAllByRole(ApplicantRole role);
}
