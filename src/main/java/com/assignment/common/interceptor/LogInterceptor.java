package com.assignment.common.interceptor;

import com.assignment.common.utils.RandomIdGenerator;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {
    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        String traceId = RandomIdGenerator.tsid();
        MDC.put(TRACE_ID_KEY, traceId);

        String method = req.getMethod();
        String uri = req.getRequestURI();
        String rawQuery = req.getQueryString();
        String query = StringUtils.isNotBlank(rawQuery) ? "?" + rawQuery : "";
        String ip = Optional.ofNullable(req.getHeader("X-Forwarded-For"))
            .map(x -> x.split(",")[0].trim())
            .orElse(req.getRemoteAddr());

        if (handler instanceof HandlerMethod handlerMethod) {
            String controllerName = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();

            log.info("[REQ] [{}#{}] {}  {}{}  ip={}", controllerName, methodName, method, uri, query, ip);
        } else {
            log.info("[REQ] {}  {}{}  ip={}", method, uri, query, ip);
        }

        return true;
    }

    /**
     * 요청 처리가 완료된 후 실행: MDC에 저장된 ID를 제거 (필수)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(TRACE_ID_KEY);
    }
}
