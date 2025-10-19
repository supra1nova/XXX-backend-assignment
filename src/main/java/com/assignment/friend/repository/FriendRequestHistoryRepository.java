package com.assignment.friend.repository;

import com.assignment.friend.entity.FriendRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestHistoryRepository extends JpaRepository<FriendRequestHistory, String>, FriendRequestHistoryQueryDslRepository {}
