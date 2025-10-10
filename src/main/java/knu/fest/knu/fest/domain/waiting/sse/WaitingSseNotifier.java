package knu.fest.knu.fest.domain.waiting.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WaitingSseNotifier {
    // boothId 별 구독자 목록
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /** 구독 요청 시 Emitter 생성 */
    public SseEmitter subscribe(Long boothId) {
        SseEmitter emitter = new SseEmitter(0L); // 타임아웃 없음
        emitters.computeIfAbsent(boothId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        // 연결 종료/에러 시 emitter 제거
        emitter.onCompletion(() -> remove(boothId, emitter));
        emitter.onTimeout(() -> remove(boothId, emitter));
        emitter.onError(e -> remove(boothId, emitter));

        // 초기 연결 시 ping 이벤트 (선택)
        sendTo(emitter, "connected", Map.of("boothId", boothId));

        return emitter;
    }

    /** 특정 부스의 모든 구독자에게 대기 인원 수 전달 */
    public void notifyCount(Long boothId, long teamCount) {
        List<SseEmitter> list = emitters.get(boothId);
        if (list == null || list.isEmpty()) return;

        for (SseEmitter emitter : list) {
            if (!sendTo(emitter, "waiting-count", Map.of("boothId", boothId, "teamCount", teamCount))) {
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
}
