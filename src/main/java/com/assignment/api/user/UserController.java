package com.assignment.api.user;

import com.assignment.common.model.*;
import com.assignment.domain.user.dto.UserListRequestDto;
import com.assignment.domain.user.dto.UserListResponseDto;
import com.assignment.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
        tags = {"1.Get User List API"},
        summary = "전체 사용자 조회",
        description = """
            -전체 사용자 목록을 조회합니다.
            """
    )
    @GetMapping
    public ResponseEntity<PageResponseDto<UserListResponseDto>> getUserList(
        @ParameterObject @Valid UserListRequestDto requestDto
    ) {
        PageResponseDto<UserListResponseDto> data = userService.findUserList(requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.OK, data);
    }
}
