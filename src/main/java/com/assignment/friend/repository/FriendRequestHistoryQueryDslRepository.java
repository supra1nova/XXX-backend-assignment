package com.assignment.friend.repository;

import com.assignment.friend.entity.FriendRequestHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendRequestHistoryQueryDslRepository {
    Page<FriendRequestHistory> searchHistory(Pageable pageable);
}
