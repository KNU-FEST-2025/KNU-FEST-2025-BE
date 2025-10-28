package knu.fest.knu.fest.domain.applicant.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import knu.fest.knu.fest.domain.applicant.controller.dto.ApplicantRequest;
import knu.fest.knu.fest.domain.applicant.controller.dto.ApplicantResponse;
import knu.fest.knu.fest.domain.applicant.controller.dto.ApplicantUpdateRequest;
import knu.fest.knu.fest.domain.applicant.service.ApplicantService;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/applicant")
@RequiredArgsConstructor
@Tag(name = "Applicant API", description = "이벤트 응모자 생성/조회/수정/삭제 API")
public class ApplicantController {

    private final ApplicantService applicantService;

    @PostMapping
    public ResponseDto<String> createApplicant(
            @Valid @RequestBody ApplicantRequest request
    ){
        String saved = applicantService.createApplicant(request);

        return ResponseDto.created(saved);
    }

    @GetMapping
    public ResponseEntity<List<ApplicantResponse>> getAllApplicants() {
        return ResponseEntity.ok(applicantService.getAllApplicants());
    }

    @PutMapping
    public ResponseDto<String> updateApplicant(@RequestBody ApplicantUpdateRequest request) {
        String message = applicantService.updateApplicant(request);
        return ResponseDto.ok(message);
    }

    @DeleteMapping
    public ResponseDto<String> deleteApplicant(
            @RequestParam Long studentNum
    ) {
        String message = applicantService.deleteApplicant(studentNum);
        return ResponseDto.ok(message);
    }


}
