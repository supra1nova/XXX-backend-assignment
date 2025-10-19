package com.assignment.user.controller;

import com.assignment.common.model.PageResponseDto;
import com.assignment.common.model.ResponseBody;
import com.assignment.common.model.ResponseCode;
import com.assignment.common.model.UserListResponseDto;
import com.assignment.user.dto.UserListRequestDto;
import com.assignment.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponseDto<UserListResponseDto>> getUserList(
        @ParameterObject @Valid UserListRequestDto requestDto
    ) {
        PageResponseDto<UserListResponseDto> data = userService.findUserList(requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.OK, data);
    }
}
