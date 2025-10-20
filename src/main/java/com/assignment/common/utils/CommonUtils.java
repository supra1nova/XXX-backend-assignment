package com.assignment.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CommonUtils {
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest();
    }

    public static String getIpAddr() {
        HttpServletRequest req = getRequest();
        return Optional.ofNullable(req.getHeader("X-Forwarded-For"))
            .map(x -> x.split(",")[0].trim())
            .orElse(req.getRemoteAddr());
    }

    public static void printRequestObject(HttpServletRequest request) {
        if (!log.isInfoEnabled()) {
            return;
        }
        log.debug("request-referer: {}", request.getHeader("Referer"));
        if (StringUtils.isNotBlank(request.getQueryString())) {
            log.debug("request-from   : {} ? {}", request.getServletPath(), request.getQueryString());
        } else {
            log.debug("request-from   : {}", request.getServletPath());
        }
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();

            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    log.debug("request: {}[{}] = {}", name, i, values[i]);
                }
            }
        }
    }

    public static String toCustomDateTimeString(Instant now) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendInstant(3)
            .toFormatter();

        return formatter.format(now);
    }
}
