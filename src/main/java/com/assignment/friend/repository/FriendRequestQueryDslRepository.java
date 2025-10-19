package com.assignment.friend.repository;

import com.assignment.common.model.Cursorable;
import com.assignment.friend.dto.FriendRequestListResponseDto;
import com.assignment.friend.entity.FriendRequest;

import java.util.List;
import java.util.Optional;

public interface FriendRequestQueryDslRepository {
    List<FriendRequestListResponseDto> searchFriendRequests(Long userId, Long requestUserId, Cursorable cursorable);

    Optional<FriendRequest> searchFriendRequestByFromAndToUserId(Long fromUserId, Long toUserId);

    Optional<FriendRequest> searchFriendRequestByAllIds(String requestId, Long fromUserId, Long toUserId);
}
