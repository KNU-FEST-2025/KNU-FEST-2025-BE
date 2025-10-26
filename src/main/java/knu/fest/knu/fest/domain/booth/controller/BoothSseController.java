package knu.fest.knu.fest.domain.booth.controller;

import knu.fest.knu.fest.domain.booth.service.BoothSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class BoothSseController {
    private final BoothSseService boothSseService;

    // 전체 부스 구독
    @GetMapping(value = "booth/subscribe/all", produces = "text/event-stream")
    public SseEmitter subscribeAll() {
        return boothSseService.subscribeAllBooth();
    }
}
