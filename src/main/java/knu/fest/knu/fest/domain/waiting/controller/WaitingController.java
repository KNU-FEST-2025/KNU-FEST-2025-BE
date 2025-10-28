package knu.fest.knu.fest.domain.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import knu.fest.knu.fest.domain.booth.service.BoothAuthService;
import knu.fest.knu.fest.domain.waiting.controller.dto.*;
import knu.fest.knu.fest.domain.waiting.entity.WaitingStatus;
import knu.fest.knu.fest.domain.waiting.service.WaitingService;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Waiting API",
        description = "대기열 등록/조회/삭제 API")
public class WaitingController {
    private final WaitingService waitingService;
    private final BoothAuthService boothAuthService;

    @PostMapping("/admin/waiting")
    @Operation(summary = "웨이팅 등록",
            description = "부스 ID와 닉네임, 전화번호를 이용해 대기열에 등록합니다.")
    public ResponseDto<WaitingRegisterResponse> waitingRegister(
            @Valid @RequestBody WaitingRegisterRequest request
    ) {
        boothAuthService.check(request.boothId());

        return ResponseDto.ok(waitingService.registerWaiting(request));
    }

    @PatchMapping("/admin/waiting/{boothId}/{waitingId}/cancel")
    @Operation(summary = "웨이팅 취소",
            description = "특정 부스의 특정 웨이팅을 취소합니다.")
    public ResponseDto<WaitingStatusResponse> waitingCancel(
            @PathVariable Long boothId,
            @PathVariable Long waitingId
    ) {
        boothAuthService.check(boothId);
        WaitingStatusResponse response = waitingService.waitingCancel(boothId, waitingId);

        return ResponseDto.ok(response);
    }

    @PatchMapping("/admin/waiting/{boothId}/{waitingId}/complete")
    @Operation(summary = "웨이팅 완료",
            description = "특정 부스의 특정 웨이팅을 완료 처리합니다.")
    public ResponseDto<WaitingStatusResponse> waitingComplete(
            @PathVariable Long boothId,
            @PathVariable Long waitingId
    ) {
        boothAuthService.check(boothId);
        WaitingStatusResponse response = waitingService.waitingComplete(boothId, waitingId);

        return ResponseDto.ok(response);
    }

    /**
     *  매니저 전용: 웨이팅 정보 수정 (닉네임, 전화번호)
     */
    @PatchMapping("/admin/waiting/{boothId}/{waitingId}")
    @Operation(
            summary = "웨이팅 정보 수정(매니저)",
            description = "닉네임/전화번호를 수정합니다. (권장: WAITING 상태에서만 허용)"
    )
    public ResponseDto<WaitingStatusResponse> waitingModify(
            @PathVariable Long boothId,
            @PathVariable Long waitingId,
            @Valid @RequestBody WaitingUpdateRequest request
    ) {
        boothAuthService.check(boothId);
        return ResponseDto.ok(waitingService.waitingUpdate(boothId, waitingId, request));
    }

    /**
     *  매니저 전용: 내부 관리용 대기열 조회
     */
    @GetMapping("/admin/waiting/{boothId}/manager")
    @Operation(summary = "매니저 전용 대기열 조회",
            description = "전화번호, 등록시간, 상태, 순번 등을 포함한 상세 리스트를 조회합니다. (상태 필터 가능)")
    public ResponseDto<List<PrivateWaitingListItemResponse>> waitingListPrivateQueue(
            @PathVariable Long boothId,
            @RequestParam(required = false) WaitingStatus status
    ) {
        boothAuthService.check(boothId);

        return ResponseDto.ok(waitingService.waitingListManagerQueue(boothId, status));
    }

    /**
     *  일반 사용자용: 공개 대기열 조회 (닉네임, 상태, 순번)
     */
    @GetMapping("/waiting/{boothId}/public")
    @Operation(summary = "공개 대기열 조회",
            description = "부스별 전체 대기열을 닉네임/상태/순번과 함께 조회합니다.")
    public ResponseDto<List<PublicWaitingListItemResponse>> waitingListPublicQueue(
            @PathVariable("boothId") Long boothId
    ) {
        return ResponseDto.ok(waitingService.waitingListPublicQueue(boothId));
    }

    /**
     *  일반 사용자용: 폰번호로 예약걸어둔 부스 조회 (닉네임, 상태, 순번)
     */
    @GetMapping("/waiting/phoneNumber")
    @Operation(summary = "공개 대기열 조회",
            description = "부스별 전체 대기열을 닉네임/상태/순번과 함께 조회합니다.")
    public ResponseDto<List<WaitingMyPhoneResponse>> waitingPhoneNum(
            @RequestParam("phone") String phone
    ) {
        return ResponseDto.ok(waitingService.waitingPhoneNum(phone));
    }


}
