package knu.fest.knu.fest.domain.auth.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import knu.fest.knu.fest.domain.auth.controller.dto.request.GoogleLoginRequest;
import knu.fest.knu.fest.domain.auth.controller.dto.request.KakaoLoginRequest;
import knu.fest.knu.fest.domain.auth.controller.dto.response.GoogleLoginResponse;
import knu.fest.knu.fest.domain.auth.controller.dto.response.GoogleLoginUrlResponse;
import knu.fest.knu.fest.domain.auth.controller.dto.response.KakaoLoginResponse;
import knu.fest.knu.fest.domain.auth.controller.dto.response.KakaoLoginUrlResponse;
import knu.fest.knu.fest.domain.auth.service.GoogleOAuthService;
import knu.fest.knu.fest.domain.auth.service.KakaoOAuthService;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;

    @Operation(
            summary = "[공통] 구글 로그인 URL 반환",
            description = "구글 로그인 화면 URL을 반환합니다. 해당 URL로 이동해주세요.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이동 후 사용자가 구글 로그인을 마칠 시 카카오 인증 서버는 /kakao/callback으로 redirect 시킵니다."),
            }
    )
    @GetMapping("/google/auth-uri")
    public ResponseDto<GoogleLoginUrlResponse> googleLoginUrl() {
        return ResponseDto.ok(googleOAuthService.getGoogleLoginUrl());
    }

    @Operation(
            summary = "[공통] 구글 로그인 callback 테스트",
            description = "로그인 성공 code 를 받아오기",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/google/callback")
    public ResponseDto<Void> googleCallback(
            @RequestParam String code
    ) {
        System.out.println(code);
        return ResponseDto.ok(null);
    }

    @Operation(
            summary = "[공통] 카카오 로그인 후 사용자 등록 및 jwt 발급",
            description = "로그인 성공 code 를 받아 카카오 서버로 부터 사용자 정보를 받고 그 정보를 DB에 등록한 뒤 jwt 발급",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @PostMapping("/google/login")
    public ResponseDto<GoogleLoginResponse> googleLogin(
            @RequestBody GoogleLoginRequest request
    ) {
        return ResponseDto.ok(googleOAuthService.processGoogleLogin(request));
    }


    @Operation(
            summary = "[공통] 카카오 로그인 URL 반환",
            description = "카카오 로그인 화면 URL을 반환합니다. 해당 URL로 이동해주세요.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이동 후 사용자가 카카오 로그인을 마칠 시 카카오 인증 서버는 /kakao/callback으로 redirect 시킵니다."),
            }
    )
    @GetMapping("/kakao/auth-uri")
    public ResponseDto<KakaoLoginUrlResponse> kakaoLoginUri() {
        return ResponseDto.ok(kakaoOAuthService.getKakaoAuthorizationUrl());
    }

    @Operation(
            summary = "[공통] 카카오 로그인 callback 테스트",
            description = "로그인 성공 code 를 받아오기",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/kakao/callback")
    public ResponseDto<Void> kakaoCallback(
            @RequestParam String code
    ) {
        System.out.println(code);
        return ResponseDto.ok(null);
    }

    @Operation(
        summary = "[공통] 카카오 로그인 후 사용자 등록 및 jwt 발급",
        description = "로그인 성공 code 를 받아 카카오 서버로 부터 사용자 정보를 받고 그 정보를 DB에 등록한 뒤 jwt 발급",
        responses = {
            @ApiResponse(responseCode = "200")
        }
    )
    @PostMapping("/kakao/login")
    public ResponseDto<KakaoLoginResponse> kakaoLogin(
            @RequestBody KakaoLoginRequest request
    ) {
        return ResponseDto.ok(kakaoOAuthService.processKakaoLogin(request));
    }
}
