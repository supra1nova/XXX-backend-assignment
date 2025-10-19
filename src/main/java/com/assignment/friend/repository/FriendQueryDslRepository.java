package com.assignment.friend.repository;

import com.assignment.friend.dto.FriendListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendQueryDslRepository {
    Page<FriendListResponseDto> searchFriends(Long userId, Long fromUserId, Long toUserId, Pageable pageable);

    boolean existsFriendByFromAndToUserId(Long fromUserId, Long toUserId);
}
