package knu.fest.knu.fest.domain.waiting.controller;

import knu.fest.knu.fest.domain.waiting.service.WaitingCacheService;
import knu.fest.knu.fest.domain.waiting.sse.WaitingSseNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/waiting")
public class WaitingSseController {
    private final WaitingSseNotifier notifier;
    private final WaitingCacheService cache;

    /** 공개 구독: 해당 부스의 혼잡도(대기 팀 수)를 실시간으로 수신 */
    @GetMapping(value = "/{boothId}/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable("boothId") Long boothId) {
        SseEmitter emitter = notifier.subscribe(boothId);

        long count = cache.teamCount(boothId);
        try {
            emitter.send(SseEmitter.event()
                    .name("waiting-count")
                    .data(Map.of("boothId", boothId, "teamCount", count)));
        } catch (IOException ignore) {

        }

        return emitter;
    }
}
