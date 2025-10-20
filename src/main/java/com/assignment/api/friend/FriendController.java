package com.assignment.api.friend;

import com.assignment.domain.friend.dto.*;
import com.assignment.domain.friendrequest.dto.FriendRequestAcceptRequestDto;
import com.assignment.domain.friendrequest.dto.FriendRequestCreateRequestDto;
import com.assignment.domain.friendrequest.dto.FriendRequestListRequestDto;
import com.assignment.domain.friendrequest.dto.FriendRequestListResponseDto;
import com.assignment.domain.friendrequest.service.FriendRequestService;
import com.assignment.domain.friend.service.FriendService;
import com.assignment.common.model.*;
import com.assignment.common.model.ResponseBody;
import com.assignment.common.validator.XUserId;
import com.assignment.common.webclient.TestApiService;
import com.assignment.domain.friendhistory.dto.FriendRequestHistoryListRequestDto;
import com.assignment.domain.friendhistory.dto.FriendRequestHistoryListResponseDto;
import com.assignment.domain.friendhistory.service.FriendRequestHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final FriendRequestHistoryService friendRequestHistoryService;
    private final TestApiService testApiService;

    @Operation(
        tags = {"2.1.Get Specific User Friend List API"},
        summary = "전체 친구 목록 조회",
        description = """
            -X-USER-ID는 유저 아이디를 의미합니다.<br>
            -header 내 X-USER-ID 미포함 요청시, DB 전체 친구 목록을 페이지네이션 후 반환합니다.<br>
            -header 내 X-USER-ID 포함 요청시, 해당하는 유저의 전체 친구 목록을 페이지네이션 후 반환합니다.<br>
            -sort가 빈값인 경우 기본 정렬 기준은 approved_at desc 입니다.
            """
    )
    @GetMapping()
    public ResponseEntity<PageResponseDto<FriendListResponseDto>> getSpecificUserFriendList(
        @Parameter(description = "사용자 식별 헤더", example = "2")
        @XUserId(optional = true)
        @RequestHeader(value = "X-USER-ID", required = false)
        Long userId,
        @Valid
        @ParameterObject
        FriendListRequestDto requestDto
    ) {
        PageResponseDto<FriendListResponseDto> data = friendService.selectFriendList(userId, requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.OK, data);
    }

    @Operation(
        tags = {"2.2.Get Friend Request List API"},
        summary = "특정 유저 기준 전체 친구 신청 목록 조회",
        description = """
            -X-USER-ID는 유저 아이디를 의미하며, 필수 값입니다.<br>
            -특정 유저가 수신한 친구 신청 목록을 페이지네이션 후 반환합니다.
            """
    )
    @GetMapping("/requests")
    public ResponseEntity<CursorPageResponseDto<FriendRequestListResponseDto>> getFriendRequestList(
        @Parameter(description = "사용자 식별 헤더", example = "2", required = true)
        @XUserId
        @RequestHeader("X-USER-ID")
        Long userId,
        @Valid
        @ParameterObject
        FriendRequestListRequestDto requestDto
    ) {
        CursorPageResponseDto<FriendRequestListResponseDto> data = friendRequestService.selectFriendRequestList(userId, requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.OK, data);
    }

    @Operation(
        tags = {"2.3.Get Friend Request History API"},
        summary = "전체 친구 신청 내역 목록 조회",
        description = """
            -전체 친구 신청 목록 내역을 페이지네이션 후 반환합니다.<br>
            -친구 신청/친구 신청 승인/친구 신청 거절 등의 데이터가 시간 순으로 기록됩니다.
            """
    )
    @GetMapping("/history")
    public ResponseEntity<PageResponseDto<FriendRequestHistoryListResponseDto>> getFriendRequestHistory(
        @Valid
        @ParameterObject
        FriendRequestHistoryListRequestDto requestDto
    ) {
        PageResponseDto<FriendRequestHistoryListResponseDto> data = friendRequestHistoryService.findFriendRequestHistoryList(requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.OK, data);
    }

    @Operation(
        tags = {"2.4.Submit Friend Request API"},
        summary = "친구 신청 요청",
        description = """
            -X-USER-ID는 유저 아이디를 의미하며, 필수 값입니다.<br>
            -친구가 아닌 관계인 경우, 친구 신청이 가능합니다.<br>
            -스스로에게 친구 신청을 할 수 없습니다.<br>
            -유저로 등록되지 않은 아이디에게는 친구 신청이 불가합니다.<br>
            -친구 신청 후 상대의 승인/거절 이전에는 동일한 상대에게 재신청이 불가합니다.<br>
            -상대의 승인/거절 이후에는 동일한 상대에게 재신청이 가능합니다.<br>
            -친구 신청 완료시 친구 신청 내역 목록에 자동 기록됩니다.
            """
    )
    @PostMapping("/request")
    public ResponseEntity<ResponseBody> submitFriendRequest(
        @Parameter(description = "사용자 식별 헤더", example = "2", required = true)
        @XUserId
        @RequestHeader("X-USER-ID")
        Long userId,
        @RequestBody
        FriendRequestCreateRequestDto requestDto
    ) {
        friendRequestService.processSubmitFriendRequest(userId, requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.CREATED);
    }

    @Operation(
        tags = {"2.5.Accept Friend Request API"},
        summary = "친구 신청 승인",
        description = """
            -X-USER-ID는 유저 아이디를 의미하며, 필수 값입니다.<br>
            -자신이 아닌 타유저로부터 요청받은 친구 신청을 승인할 수 있습니다.<br>
            -친구 신청 승인 완료시 친구 신청 내역 목록에 자동 기록됩니다.
            """
    )
    @PatchMapping("/accept/{requestId}")
    public ResponseEntity<ResponseBody> acceptFriendRequest(
        @Parameter(description = "사용자 식별 헤더", example = "2", required = true)
        @XUserId
        @RequestHeader("X-USER-ID")
        Long userId,
        @Valid
        @Parameter(description = "친구 신청 식별값", example = "8bc3930e-e314-4129-b401-8b31475f0606", in = ParameterIn.PATH)
        @PathVariable("requestId")
        String requestId,
        @RequestBody
        FriendRequestAcceptRequestDto requestDto
    ) {
        friendRequestService.processAcceptFriendRequest(userId, requestId, requestDto);
        return ResponseBody.toResponseEntity(ResponseCode.OK);
    }

    @Operation(
        tags = {"2.6.Reject Friend Request API",},
        summary = "친구 신청 거절",
        description = """
            -X-USER-ID는 유저 아이디를 의미하며, 필수 값입니다.<br>
            -자신이 아닌 타유저로부터 요청받은 친구 신청을 거절할 수 있습니다.<br>
            -친구 신청 거절 완료시 친구 신청 내역 목록에 자동 기록됩니다.
            """
    )
    @PatchMapping("/reject/{requestId}")
    public ResponseEntity<ResponseBody> rejectFriendRequest(
        @Parameter(description = "사용자 식별 헤더", example = "2", required = true)
        @XUserId
        @RequestHeader("X-USER-ID")
        Long userId,
        @Valid
        @Parameter(description = "친구 신청 식별값", example = "706626f9-86fd-415f-b38d-99cd0f81de64", in = ParameterIn.PATH)
        @PathVariable("requestId")
        String requestId
    ) {
        friendRequestService.processRejectFriendRequest(userId, requestId);
        return ResponseBody.toResponseEntity(ResponseCode.OK);
    }

    @Operation(
        tags = {"3.Rate Limit Test API"},
        summary = "1초/10회 이상 클릭 제한",
        description = """
            -userId 가 2인 유저가 userId 가 20인 유저에게 20번의 요청하는 상황 가정합니다.<br>
            -rate limit 로 1초에 10회 제한합니다.<br>
            -동시 요청을 분산락으로 처리해 같은 순간 여러 요청이 동시에 들어오는 상황을 제한합니다.<br>
            """
    )
    @PostMapping("/test")
    public ResponseEntity<ResponseBody> rateLimitTest() {
        for (int i = 0; i < 20; i++) {
            testApiService.postCreateFriendRequestTest();
        }
        return ResponseBody.toResponseEntity(ResponseCode.OK);
    }
}
