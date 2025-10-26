package knu.fest.knu.fest.domain.booth.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class BoothSseService {
    private final List<SseEmitter> allBoothEmitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public BoothSseService() {
        // heartbeat: 15초마다 ping 전송
        scheduler.scheduleAtFixedRate(() -> {
            allBoothEmitters.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            });
        }, 0, 15, TimeUnit.SECONDS);
    }

    public SseEmitter subscribeAllBooth() {
        SseEmitter emitter = new SseEmitter(60_000L); // 60초 후 timeout
        allBoothEmitters.add(emitter);

        emitter.onCompletion(() -> allBoothEmitters.remove(emitter));
        emitter.onTimeout(() -> {
            allBoothEmitters.remove(emitter);
            emitter.complete();
        });
        emitter.onError((e) -> allBoothEmitters.remove(emitter));

        return emitter;
    }

    public void sendAllBoothUpdate(Long boothId, Long likeCount, Long waitingCount) {
        allBoothEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("booth-update")
                        .data(Map.of(
                                "boothId", boothId,
                                "likeCount", likeCount,
                                "waitingCount", waitingCount
                        )));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
    }

}
