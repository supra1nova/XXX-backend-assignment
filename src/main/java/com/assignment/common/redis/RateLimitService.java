package com.assignment.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final StringRedisTemplate redisTemplate;

    // 🔹 1. 속도 제한 (Rate Limit)
    //@formatter:off
    private static final String RATE_LIMIT_SCRIPT = """
        local current = redis.call('incr', KEYS[1])
        if tonumber(current) == 1 then
            redis.call('pexpire', KEYS[1], ARGV[1])
        end
        return current
    """;

    public boolean checkRateLimit(String key, int limit, long ttlMillis) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(RATE_LIMIT_SCRIPT, Long.class);
        Long count = redisTemplate.execute(script, Collections.singletonList(key), String.valueOf(ttlMillis));
        return count != null && count <= limit;
    }

    // 🔹 2. 동시 요청 제한 (Lock)
    public boolean tryLock(String key, long ttlMillis) {
        Boolean success = redisTemplate.opsForValue()
            .setIfAbsent(key, "LOCKED", Duration.ofMillis(ttlMillis));
        return Boolean.TRUE.equals(success);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }
}
