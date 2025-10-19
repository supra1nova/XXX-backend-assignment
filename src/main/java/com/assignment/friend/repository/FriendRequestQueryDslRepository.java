package com.assignment.friend.repository;

import com.assignment.common.model.Cursorable;
import com.assignment.friend.dto.FriendRequestListResponseDto;

import java.util.List;

public interface FriendRequestQueryDslRepository {
    List<FriendRequestListResponseDto> searchFriendRequests(Long userId, Long requestUserId, Cursorable cursorable);
}
