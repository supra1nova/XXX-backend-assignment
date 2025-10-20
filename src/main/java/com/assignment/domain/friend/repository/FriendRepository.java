package com.assignment.domain.friend.repository;

import com.assignment.domain.friend.entity.Friend;
import com.assignment.domain.friend.entity.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, FriendId>, FriendQueryDslRepository {}
