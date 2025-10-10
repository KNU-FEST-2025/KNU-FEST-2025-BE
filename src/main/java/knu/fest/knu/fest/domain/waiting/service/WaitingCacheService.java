package knu.fest.knu.fest.domain.waiting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingCacheService {

    private final StringRedisTemplate redis;


    private String keyQueue(Long boothId)   { return "waiting:booth:" + boothId + ":queue"; }
    private String keyStatus(Long waitingId){ return "waiting:waitingID:" + waitingId + ":status"; }

    public void enqueue(Long boothId, Long waitingId) {
        redis.opsForList().rightPush(keyQueue(boothId), waitingId.toString());
        redis.opsForValue().set(keyStatus(waitingId), "WAITING");
    }

    public void remove(Long boothId, Long waitingId, String status) {
        redis.opsForList().remove(keyQueue(boothId), 1, waitingId.toString());
        redis.opsForValue().set(keyStatus(waitingId), status); // "CANCELLED"/"DONE"
    }

    public long teamCount(Long boothId) {
        Long len = redis.opsForList().size(keyQueue(boothId));
        return len != null ? len : 0L;
    }

    // 닉네임 목록용: 대기열의 waitingId를 범위로 조회
    public List<Long> rangeIds(Long boothId, long offset, long limit) {
        long end = (limit <= 0 ? -1 : offset + limit - 1);
        List<String> raw = redis.opsForList().range(keyQueue(boothId), offset, end);
        if (raw == null) return List.of();
        return raw.stream().map(Long::valueOf).toList();
    }

    // 관리용: 대기열의 모든 waitingId 조회
    public List<Long> rangeAll(Long boothId) {
        List<String> raw = redis.opsForList().range(keyQueue(boothId), 0, -1);
        if (raw == null) return List.of();
        return raw.stream().map(Long::valueOf).toList();
    }
}
