package com.assignment.common.exception;

import com.assignment.common.model.ResponseCode;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class ApiErrorController {
    @RequestMapping("/error")
    public <T> ResponseEntity<T> handleError() {
        throw new CustomException(ResponseCode.PATH_NOT_FOUND, new RuntimeException());
    }
}
