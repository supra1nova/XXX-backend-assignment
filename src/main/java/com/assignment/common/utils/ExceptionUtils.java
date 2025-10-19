package com.assignment.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionUtils {
    public static void printException(String exceptionName, HttpServletRequest req, Exception e) {
        if (!log.isInfoEnabled()) {
            return;
        }
        log.error("{} : {} {} ({})", exceptionName, e.getClass().getName(), e.getMessage(), req.getServletPath());
        // todo: 삭제할 것
//        e.printStackTrace();
    }
}
