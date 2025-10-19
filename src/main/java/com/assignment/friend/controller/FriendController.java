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
import org.hibernate.validator.constraints.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
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
        @RequestHeader("X-USER-ID") Long userId,
        @ParameterObject @Valid FriendRequestListRequestDto requestDto
    ) {
        CursorPageResponseDto<FriendRequestListResponseDto> data = friendRequestService.selectFriendRequestList(userId, requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.OK, data);
    }

    @PostMapping("/request")
    public ResponseEntity<ResponseBody> submitFriendRequest(
        // todo: userId header 검증
        @RequestHeader("X-USER-ID") Long userId,
        @RequestBody FriendRequestCreateRequestDto requestDto
    ) {
        friendRequestService.processSubmitFriendRequest(userId, requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.CREATED);
    }

    @PatchMapping("/accept/{requestId}")
    public ResponseEntity<ResponseBody> acceptFriendRequest(
        // todo: userId header 검증
        @RequestHeader("X-USER-ID") Long userId,
        @UUID @PathVariable("requestId") String requestId,
        @RequestBody FriendRequestAcceptRequestDto requestDto
    ) {
        friendRequestService.processAcceptFriendRequest(userId, requestId, requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.OK);
    }

    @PatchMapping("/reject/{requestId}")
    public ResponseEntity<ResponseBody> rejectFriendRequest(
        // todo: userId header 검증
        @RequestHeader("X-USER-ID") Long userId,
        @UUID @PathVariable("requestId") String requestId
    ) {
        friendRequestService.processRejectFriendRequest(userId, requestId);
        return ResponseBody.toResponseEntity(ResponseCode.OK);
    }
}
