package knu.fest.knu.fest.domain.applicant.service;

import knu.fest.knu.fest.domain.applicant.controller.dto.ApplicantRequest;
import knu.fest.knu.fest.domain.applicant.controller.dto.ApplicantResponse;
import knu.fest.knu.fest.domain.applicant.controller.dto.ApplicantUpdateRequest;
import knu.fest.knu.fest.domain.applicant.entity.Applicant;
import knu.fest.knu.fest.domain.applicant.entity.ApplicantRole;
import knu.fest.knu.fest.domain.applicant.repository.ApplicantRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicantService {

    private final ApplicantRepository applicantRepository;

    public String createApplicant(ApplicantRequest request) {
        boolean exists = applicantRepository.findByStudentNumAndRole(request.studentNum(), request.role()).isPresent();

        if (exists) {
            throw new CommonException(ErrorCode.ALREADY_EXIST_APPLICANT);
        }
        Applicant saved = applicantRepository.save(request.toEntity());

        return "등록완료";
    }

    @Transactional(readOnly = true)
    public List<ApplicantResponse> getRoleApplicants(ApplicantRole role) {
        return applicantRepository.findAllByRoleOrderByModifiedAtDesc(role)
                .stream()
                .map(ApplicantResponse::from)
                .toList();
    }

    public String updateApplicant(ApplicantUpdateRequest request) {
        return applicantRepository.findById(request.id())
                .map(applicant -> {
                    applicant.update(
                            request.name(),
                            request.studentNum(),
                            request.department(),
                            request.role()
                    );
                    return "수정완료";
                })
                .orElse("해당 학번의 응모자를 찾을 수 없습니다.");
    }


    public String deleteApplicant(Long id) {
        return applicantRepository.findById(id)
                .map(applicant -> {
                    applicantRepository.delete(applicant);
                    return "삭제완료";
                })
                .orElse("해당 학과/학번의 응모자를 찾을 수 없습니다.");
    }
}
