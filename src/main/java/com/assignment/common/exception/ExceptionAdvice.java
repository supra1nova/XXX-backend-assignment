package com.assignment.common.exception;

import com.assignment.common.model.ResponseBody;
import com.assignment.common.model.ResponseCode;
import com.assignment.common.utils.CommonUtils;
import com.assignment.common.utils.ExceptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(CustomException.class)
    protected Object handleCustomException(HttpServletRequest req, CustomException e) {
        CommonUtils.printRequestObject(req);
        ExceptionUtils.printException("CustomException", req, e);
        return resp(e.getResponseCode(), e);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    protected Object handleRateLimitExceededException(HttpServletRequest req, RateLimitExceededException e) {
        CommonUtils.printRequestObject(req);
        ExceptionUtils.printException("RateLimitExceededException", req, e);
        return resp(ResponseCode.RATE_LIMIT_EXCEEDED, e);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public Object handleMissingRequestHeaderException(HttpServletRequest req, MissingRequestHeaderException e) {
        ExceptionUtils.printException("MissingRequestHeaderException", req, e);
        return resp(ResponseCode.INVALID_HEADER_VALUE, e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgumentException(HttpServletRequest req, IllegalArgumentException e) {
        ExceptionUtils.printException("IllegalArgumentException", req, e);
        return resp(ResponseCode.INVALID_VALUES, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        ExceptionUtils.printException("MethodArgumentNotValidException", req, e);
        return resp(ResponseCode.INVALID_VALUES, e);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFoundException(HttpServletRequest req, NoResourceFoundException e) {
        ExceptionUtils.printException("NoResourceFoundException", req, e);
        return resp(ResponseCode.PATH_NOT_FOUND, e);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected Object handleNoHandlerFound(HttpServletRequest req, NoHandlerFoundException e) {
        ExceptionUtils.printException("NoHandlerFoundException", req, e);
        return resp(ResponseCode.PATH_NOT_FOUND, e);
    }

    @ExceptionHandler(DataAccessException.class)
    protected Object handleDataAccessException(HttpServletRequest req, DataAccessException e) {
        ExceptionUtils.printException("DataAccessException", req, e);
        return resp(ResponseCode.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected Object handleHttpMessageNotReadable(HttpServletRequest req, HttpMessageNotReadableException e) {
        ExceptionUtils.printException("HttpMessageNotReadableException", req, e);
        return resp(ResponseCode.INVALID_REQUEST, e);
    }

    @ExceptionHandler(Exception.class)
    protected Object handleException(HttpServletRequest req, Exception e) {
        ExceptionUtils.printException("Exception", req, e);
        return resp(ResponseCode.INTERNAL_SERVER_ERROR, e);
    }

    protected Object resp(ResponseCode code, Exception e) {
        return ResponseBody.toErrorResponseEntity(code, e);
    }
}
