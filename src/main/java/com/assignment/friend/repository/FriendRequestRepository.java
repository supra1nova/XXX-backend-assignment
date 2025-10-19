package com.assignment.friend.repository;

import com.assignment.friend.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, String>, FriendRequestQueryDslRepository {}
