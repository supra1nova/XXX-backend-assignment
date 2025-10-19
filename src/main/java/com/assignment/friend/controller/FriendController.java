package com.assignment.friend.controller;

import com.assignment.common.model.CursorPageResponseDto;
import com.assignment.common.model.PageResponseDto;
import com.assignment.common.model.ResponseBody;
import com.assignment.common.model.ResponseCode;
import com.assignment.friend.dto.*;
import com.assignment.friend.service.FriendRequestService;
import com.assignment.friend.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final FriendRequestService friendRequestService;

    @GetMapping()
    public ResponseEntity<PageResponseDto<FriendListResponseDto>> getSpecificUserFriendList(
        // todo: userId header 검증
        @RequestHeader(value = "X-USER-ID", required = false) Long userId,
        @ParameterObject @Valid FriendListRequestDto requestDto
    ) {
        PageResponseDto<FriendListResponseDto> data = friendService.selectFriendList(userId, requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.OK, data);
    }

    @GetMapping("/requests")
    public ResponseEntity<CursorPageResponseDto<FriendRequestListResponseDto>> getFriendRequestList(
        // todo: userId header 검증
        @RequestHeader(value = "X-USER-ID") Long userId,
        @ParameterObject @Valid FriendRequestListRequestDto requestDto
    ) {
        CursorPageResponseDto<FriendRequestListResponseDto> data = friendRequestService.selectFriendRequestList(userId, requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.OK, data);
    }

    @PostMapping()
    public ResponseEntity<ResponseBody> createFriendRequest(
        // todo: userId header 검증
        @RequestHeader(value = "X-USER-ID") Long userId,
        @RequestBody() FriendRequestCreateRequestDto requestDto
    ) {
        friendRequestService.insertFriendRequest(userId, requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.CREATED);
    }
}
