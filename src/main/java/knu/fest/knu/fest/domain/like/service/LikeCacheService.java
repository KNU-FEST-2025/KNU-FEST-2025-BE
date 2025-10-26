package knu.fest.knu.fest.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeCacheService {

    private final StringRedisTemplate redis;

    private String keyQueue(Long boothId)   { return "waiting:booth:" + boothId + ":queue"; }

    public void enqueue(Long boothId, Long likeId) {
        redis.opsForList().rightPush(keyQueue(boothId), likeId.toString());
    }

    public void remove(Long boothId, Long likeId) {
        redis.opsForList().remove(keyQueue(boothId), 1, likeId.toString());
    }

    public long count(Long boothId) {
        Long len = redis.opsForList().size(keyQueue(boothId));
        return len != null ? len : 0L;
    }

}
