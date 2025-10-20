package com.assignment.domain.friendhistory.repository;

import com.assignment.domain.friendhistory.entity.FriendRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestHistoryRepository extends JpaRepository<FriendRequestHistory, String>, FriendRequestHistoryQueryDslRepository {}
