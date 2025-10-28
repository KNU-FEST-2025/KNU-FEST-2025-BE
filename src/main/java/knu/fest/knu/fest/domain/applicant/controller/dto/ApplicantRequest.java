package knu.fest.knu.fest.domain.applicant.controller.dto;

import knu.fest.knu.fest.domain.applicant.entity.Applicant;
import knu.fest.knu.fest.domain.applicant.entity.ApplicantRole;

public record ApplicantRequest(
        String name,
        Long studentNum,
        String department,
        ApplicantRole role
) {
    public Applicant toEntity() {
        return Applicant.builder()
                .name(name)
                .studentNum(studentNum)
                .department(department)
                .role(role)
                .build();
    }
}
