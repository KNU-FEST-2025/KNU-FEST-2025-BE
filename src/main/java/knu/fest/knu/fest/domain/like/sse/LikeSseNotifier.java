package knu.fest.knu.fest.domain.like.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class LikeSseNotifier {

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public LikeSseNotifier() {
        // 15초마다 ping 전송
        scheduler.scheduleAtFixedRate(() -> {
            emitters.values().forEach(list -> {
                for (SseEmitter emitter : list) {
                    try {
                        emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
                    } catch (IOException e) {
                        // 실패하면 emitter 제거
                        removeEmitter(emitter);
                    }
                }
            });
        }, 0, 15, TimeUnit.SECONDS);
    }

    public SseEmitter likeSubscribe(Long boothId) {
        // 1분 타임아웃
        SseEmitter emitter = new SseEmitter(300_000L);
        emitters.computeIfAbsent(boothId, k -> new CopyOnWriteArrayList<>()).add(emitter);


        // 연결 종료/에러 시 emitter 제거
        emitter.onCompletion(() -> remove(boothId, emitter));
        emitter.onTimeout(() -> {
            remove(boothId, emitter);
            emitter.complete();
        });
        emitter.onError(e -> remove(boothId, emitter));

        // 초기 연결 시 ping 이벤트 (선택)
        sendTo(emitter, "connected", Map.of("boothId", boothId));

        return emitter;
    }

    public void notifyCount(Long boothId, long count) {
        List<SseEmitter> list = emitters.get(boothId);
        if (list == null || list.isEmpty()) return;

        for (SseEmitter emitter : list) {
            if (!sendTo(emitter, "like-count", Map.of("boothId", boothId, "Count", count))) {
                remove(boothId, emitter);
            }
        }
    }


    private boolean sendTo(SseEmitter emitter, String event, Object data) {
        try {
            emitter.send(SseEmitter.event().name(event).data(data));
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    private void remove(Long boothId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(boothId);
        if (list != null) list.remove(emitter);
    }

    private void removeEmitter(SseEmitter emitter) {
        emitters.values().forEach(list -> list.remove(emitter));
    }
}
