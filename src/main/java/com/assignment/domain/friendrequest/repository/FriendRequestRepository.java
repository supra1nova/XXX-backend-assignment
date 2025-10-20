package com.assignment.domain.friendrequest.repository;

import com.assignment.domain.friendrequest.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, String>, FriendRequestQueryDslRepository {}
