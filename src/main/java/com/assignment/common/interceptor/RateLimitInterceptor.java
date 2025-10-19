package com.assignment.common.interceptor;

import com.assignment.common.exception.AnotherRequestProcessingException;
import com.assignment.common.exception.RateLimitExceededException;
import com.assignment.common.model.ResponseCode;
import com.assignment.common.redis.RateLimitService;
import com.assignment.common.utils.CommonUtils;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    private static final String X_USER_ID = "X-USER-ID";
    @Value("${spring.application.name}")
    private String app;
    @Value("${custom.redis.rate-limit}")
    private int rateLimit;
    @Value("${custom.redis.rate-ttl}")
    private long rateTtl;
    @Value("${custom.redis.lock-ttl}")
    private long lockTtl;

    private final RateLimitService rateLimitService;

    private final ThreadLocal<String> lockKeyHolder = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
        throws Exception {

        String headerValue = req.getHeader(X_USER_ID);
        String userId = StringUtils.isNotBlank(headerValue) ? headerValue : "anonymous";
        String ip = CommonUtils.getIpAddr();
        String path = req.getRequestURI();

        // 식별 키
        String baseKey = String.format("limit:%s:%s:%s", userId, ip, path);
        String rateKey = "rate:" + baseKey;
        String lockKey = "lock:" + baseKey;

        // 1️⃣ 속도 제한 (1초에 10회 초과 시 차단)
        boolean rateAllowed = rateLimitService.checkRateLimit(rateKey, rateLimit, rateTtl);
        if (!rateAllowed) {
            throw new RateLimitExceededException(ResponseCode.RATE_LIMIT_EXCEEDED.getDetail());
        }

        // 2️⃣ 동시 요청 제한 (락 걸기)
        boolean lockAcquired = rateLimitService.tryLock(lockKey, lockTtl);
        if (!lockAcquired) {
            throw new AnotherRequestProcessingException(ResponseCode.RATE_LIMIT_EXCEEDED.getDetail());
        }

        // 요청이 통과되면, 락 해제를 위해 ThreadLocal에 저장
        lockKeyHolder.set(lockKey);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        String lockKey = lockKeyHolder.get();
        if (lockKey != null) {
            rateLimitService.releaseLock(lockKey);
            lockKeyHolder.remove();
        }
    }
}
