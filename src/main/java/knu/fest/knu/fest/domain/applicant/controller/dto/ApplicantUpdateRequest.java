package knu.fest.knu.fest.domain.applicant.controller.dto;

import knu.fest.knu.fest.domain.applicant.entity.ApplicantRole;

public record ApplicantUpdateRequest(
        String name,
        Long studentNum,     // 수정 기준 학번 (기존 학번)
        String department,
        ApplicantRole role
) { }