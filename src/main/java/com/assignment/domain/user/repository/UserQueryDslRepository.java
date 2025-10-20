package com.assignment.domain.user.repository;

import com.assignment.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryDslRepository {
    Page<User> searchUsers(String userName, Integer userAge, Pageable pageable);
}
