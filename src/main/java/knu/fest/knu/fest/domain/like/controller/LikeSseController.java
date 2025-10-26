package knu.fest.knu.fest.domain.like.controller;


import knu.fest.knu.fest.domain.like.service.LikeCacheService;
import knu.fest.knu.fest.domain.like.sse.LikeSseNotifier;
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
@RequestMapping("/api/v1/like")
public class LikeSseController {

    private final LikeSseNotifier notifier;
    private final LikeCacheService cache;

    @GetMapping(value = "/{boothId}/subscribe", produces = "text/event-stream")
    public SseEmitter likeSubscribe(@PathVariable Long boothId) {
        SseEmitter emitter = notifier.likeSubscribe(boothId);

        long count = cache.count(boothId);
        try {
            emitter.send(SseEmitter.event()
                    .name("waiting-count")
                    .data(Map.of("boothId", boothId, "Count", count)));
        } catch (IOException ignore) {

        }
        return emitter;
    }

}
