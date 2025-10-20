package com.assignment.domain.friendrequest.service;

import com.assignment.domain.friendrequest.dto.FriendRequestAcceptRequestDto;
import com.assignment.domain.friendrequest.dto.FriendRequestListRequestDto;
import com.assignment.domain.friendrequest.dto.FriendRequestListResponseDto;
import com.assignment.domain.friend.entity.Friend;
import com.assignment.domain.friendrequest.entity.FriendRequest;
import com.assignment.domain.friendrequest.repository.FriendRequestRepository;
import com.assignment.common.exception.CustomException;
import com.assignment.common.model.CursorPageResponseDto;
import com.assignment.common.model.Cursorable;
import com.assignment.common.model.ResponseCode;
import com.assignment.domain.friendrequest.dto.FriendRequestCreateRequestDto;
import com.assignment.domain.friendhistory.entity.FriendRequestHistory;
import com.assignment.domain.friendhistory.entity.FriendRequestStatus;
import com.assignment.domain.friend.repository.FriendRepository;
import com.assignment.domain.friendhistory.repository.FriendRequestHistoryRepository;
import com.assignment.domain.user.entity.User;
import com.assignment.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final FriendRequestHistoryRepository friendRequestHistoryRepository;
    private final UserRepository userRepository;

    public CursorPageResponseDto<FriendRequestListResponseDto> selectFriendRequestList(
        Long userId,
        FriendRequestListRequestDto requestDto
    ) {
        requestDto.init();
        Cursorable cursorable = requestDto.toCursorable(requestDto.getSort());

        List<FriendRequestListResponseDto> friendRequestList = friendRequestRepository.searchFriendRequests(
            userId,
            requestDto.getRequestUserId(),
            cursorable
        );

        return CursorPageResponseDto.of(friendRequestList, requestDto);
    }

    public void processSubmitFriendRequest(Long fromUserId, FriendRequestCreateRequestDto requestDto) {
        Long toUserId = requestDto.getToUserId();
        if (Objects.equals(fromUserId, toUserId)) {
            throw new CustomException(ResponseCode.CANNOT_SELF_FRIEND_REQUEST);
        }

        User toUser = userRepository.findById(toUserId).orElseThrow(() -> new CustomException(ResponseCode.RECEIVER_NOT_FOUND));

        boolean existsFriend = friendRepository.existsFriendByFromAndToUserId(fromUserId, toUserId);
        if (existsFriend) {
            throw new CustomException(ResponseCode.ALREADY_FRIENDS);
        }

        Optional<FriendRequest> existingRequestOpt =
            friendRequestRepository.searchFriendRequestByFromAndToUserIds(fromUserId, toUserId);
        if (existingRequestOpt.isPresent()) {
            FriendRequest friendRequest = existingRequestOpt.get();

            if (friendRequest.getFromUser().getUserId().equals(fromUserId)) {
                throw new CustomException(ResponseCode.FRIEND_REQUEST_ALREADY_SENT);
            } else {
                throw new CustomException(ResponseCode.FRIEND_REQUEST_ALREADY_RECEIVED);
            }
        }

        FriendRequest friendRequest = FriendRequest.create(userRepository.getReferenceById(fromUserId), toUser);
        friendRequestRepository.save(friendRequest);

        insertFriendRequestHistory(friendRequest, FriendRequestStatus.PENDING);
    }

    public void processAcceptFriendRequest(Long toUserId, String requestId, FriendRequestAcceptRequestDto requestDto) {
        if (StringUtils.isBlank(requestId) || requestDto.getFromUserId() == null) {
            throw new CustomException(ResponseCode.INVALID_VALUES);
        }

        FriendRequest friendRequest = friendRequestRepository.searchFriendRequestByAllIds(
            requestId,
            requestDto.getFromUserId(),
            toUserId
        ).orElseThrow(() -> new CustomException(ResponseCode.FRIEND_REQUEST_NOT_FOUND));

        friendRepository.save(Friend.create(friendRequest.getFromUser(), friendRequest.getFromUser(), friendRequest.getToUser()));
        friendRepository.save(Friend.create(friendRequest.getToUser(), friendRequest.getFromUser(), friendRequest.getToUser()));

        insertFriendRequestHistory(friendRequest, FriendRequestStatus.ACCEPTED);

        friendRequestRepository.delete(friendRequest);
    }

    public void processRejectFriendRequest(Long toUserId, String requestId) {
        if (StringUtils.isBlank(requestId)) {
            throw new CustomException(ResponseCode.INVALID_VALUES);
        }

        FriendRequest friendRequest = friendRequestRepository.searchFriendRequestByRequestAndToUserIds(
            requestId,
            toUserId
        ).orElseThrow(() -> new CustomException(ResponseCode.FRIEND_REQUEST_NOT_FOUND));

        insertFriendRequestHistory(friendRequest, FriendRequestStatus.REJECTED);

        friendRequestRepository.delete(friendRequest);
    }

    private void insertFriendRequestHistory(FriendRequest friendRequest, FriendRequestStatus status) {
        FriendRequestHistory friendRequestHistory = FriendRequestHistory.create(
            friendRequest,
            friendRequest.getFromUser(),
            friendRequest.getToUser(),
            status
        );
        friendRequestHistoryRepository.save(friendRequestHistory);
    }
}
