package com.assignment.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final StringRedisTemplate redisTemplate;

    /**
     * @param key       요청 구분 키 (userId/ip/path 조합)
     * @param limit     허용 요청 수
     * @param ttlMillis 기간(ms)
     * @return true: 허용 / false: 초과
     */
    public boolean checkLimit(String key, int limit, long ttlMillis) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        // 원자적 증가
        Long count = ops.increment(key);

        if (count == 1) {
            // 첫 요청이면 TTL 설정 (예: 1초)
            redisTemplate.expire(key, Duration.ofMillis(ttlMillis));
        }

        return count <= limit;
    }
}
