package knu.fest.knu.fest.domain.applicant.controller.dto;

import knu.fest.knu.fest.domain.applicant.entity.Applicant;
import knu.fest.knu.fest.domain.applicant.entity.ApplicantRole;

public record ApplicantResponse(
        Long id,
        String name,
        Long studentNum,
        String department,
        ApplicantRole role
) {
    public static ApplicantResponse from(Applicant applicant) {
        return new ApplicantResponse(
                applicant.getId(),
                applicant.getName(),
                applicant.getStudentNum(),
                applicant.getDepartment(),
                applicant.getRole()
        );
    }
}
