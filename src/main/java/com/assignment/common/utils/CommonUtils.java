package com.assignment.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
public class CommonUtils {
    /**
     * request parameter 출력
     *
     * @param request HttpServletRequest
     */
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
}
