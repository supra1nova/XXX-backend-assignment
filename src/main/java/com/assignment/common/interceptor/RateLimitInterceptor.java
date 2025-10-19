package com.assignment.common.interceptor;

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
    private int app;
    @Value("${custom.redis.limit}")
    private int limit;
    @Value("${custom.redis.TTL}")
    private long ttl;

    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String header = req.getHeader(X_USER_ID);
        String userId = StringUtils.isNotBlank(header) ? header : "anonymous";
        String ip = CommonUtils.getIpAddr();
        String path = req.getRequestURI();

        String key = String.format("rate:%s:%s:%s:%s", app, userId, ip, path);

        boolean allowed = rateLimitService.checkLimit(key, limit, ttl);

        if (!allowed) {
            throw new RateLimitExceededException(ResponseCode.RATE_LIMIT_EXCEEDED.getDetail());
        }

        return true;
    }
}
