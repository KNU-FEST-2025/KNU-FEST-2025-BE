package knu.fest.knu.fest.domain.booth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import knu.fest.knu.fest.domain.booth.controller.dto.BoothCreateRequest;
import knu.fest.knu.fest.domain.booth.controller.dto.BoothCreateResponse;
import knu.fest.knu.fest.domain.booth.controller.dto.BoothDetailResponse;
import knu.fest.knu.fest.domain.booth.controller.dto.BoothUpdateRequest;
import knu.fest.knu.fest.domain.booth.service.BoothService;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/booth")
@RequiredArgsConstructor
@Tag(name = "Booth API", description = "축제 부스 생성/조회/수정 API")
public class BoothController {

    private final BoothService boothService;

    @Operation(
            summary = "부스 생성",
            description = """
            새로운 축제 부스를 등록합니다.  
            - ADMIN 권한이 있어야 호출 가능합니다.  
            - 부스 번호(`boothNumber`)는 중복될 수 없습니다.  
            """
    )
    @PostMapping
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    public ResponseDto<BoothCreateResponse> createBooth(
            @Valid @RequestBody BoothCreateRequest request
    ) {
        BoothCreateResponse response = boothService.createBooth(request);
        return ResponseDto.created(response);
    }

    @Operation(
            summary = "부스 수정",
            description = """
            기존 축제 부스 정보를 수정합니다.  
            - ADMIN 권한이 있어야 호출 가능합니다.  -> 추후 부스 관리자 권한으로 변경
            - 부스 번호(`boothNumber`)는 중복될 수 없습니다.  
            - 일부 필드만 수정할 수도 있습니다. (null 로 보내면 해당 필드는 수정하지 않음)  
            """
    )
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseDto<BoothDetailResponse> updateBooth(
            @PathVariable Long id,
            @Valid @RequestBody BoothUpdateRequest request
    ) {
        BoothDetailResponse response = boothService.updateBooth(id, request);

        return ResponseDto.ok(response);
    }

    @Operation(
            summary = "부스 상세 조회",
            description = """
            특정 축제 부스의 상세 정보를 조회합니다.  
            - 모든 사용자(비로그인 포함)가 호출 가능합니다.  
            """
    )
    @GetMapping("/{id}")
    public ResponseDto<BoothDetailResponse> getBooth(
            @PathVariable Long id
    ) {
        BoothDetailResponse response = boothService.getBooth(id);

        return ResponseDto.ok(response);
    }
}
