package com.assignment.friend.service;

import com.assignment.common.exception.CustomException;
import com.assignment.common.model.CursorPageResponseDto;
import com.assignment.common.model.Cursorable;
import com.assignment.common.model.ResponseCode;
import com.assignment.friend.dto.FriendRequestCreateRequestDto;
import com.assignment.friend.dto.FriendRequestListRequestDto;
import com.assignment.friend.dto.FriendRequestListResponseDto;
import com.assignment.friend.entity.FriendRequest;
import com.assignment.friend.repository.FriendRepository;
import com.assignment.friend.repository.FriendRequestRepository;
import com.assignment.user.entity.User;
import com.assignment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void insertFriendRequest(Long fromUserId, FriendRequestCreateRequestDto requestDto) {
        // todo: interceptor 내 x-user-id 확인 로직 구현시 삭제
        User fromUser = userRepository.findById(fromUserId)
            .orElseThrow(() -> new CustomException(ResponseCode.X_USER_ID_NOT_FOUND));

        Long toUserId = requestDto.getToUserId();
        if (Objects.equals(fromUserId, toUserId)) {
            throw new CustomException(ResponseCode.CANNOT_SELF_FRIEND_REQUEST);
        }

        User toUser = selectUserByToUserId(toUserId);

        friendRepository.existsFriendByFromAndToUserId(fromUserId, toUserId);

        Optional<FriendRequest> existingRequestOpt =
            friendRequestRepository.searchFriendRequestByFromAndToUserId(fromUserId, toUserId);
        if (existingRequestOpt.isPresent()) {
            FriendRequest friendRequest = existingRequestOpt.get();

            if (friendRequest.getFromUser().getUserId().equals(fromUserId)) {
                throw new CustomException(ResponseCode.FRIEND_REQUEST_ALREADY_SENT);
            } else {
                throw new CustomException(ResponseCode.FRIEND_REQUEST_ALREADY_RECEIVED);
            }
        }

        // todo: interceptor 내 x-user-id 확인 로직 구현시 fromUser 를 userRepository.getReferenceById(fromUserId);로 교체
        FriendRequest friendRequest = FriendRequest.create(
            fromUser,
            toUser
        );

        friendRequestRepository.save(friendRequest);
    }

    private User selectUserByToUserId(Long toUserId) {
        Optional<User> toUser = userRepository.findById(toUserId);
        if (toUser.isEmpty()) {
            throw new CustomException(ResponseCode.RECEIVER_NOT_FOUND);
        }

        return toUser.get();
    }
}
