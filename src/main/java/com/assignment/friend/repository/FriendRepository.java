package com.assignment.friend.repository;

import com.assignment.friend.entity.Friend;
import com.assignment.friend.entity.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, FriendId>, FriendQueryDslRepository {}
