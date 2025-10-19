package com.assignment.user.repository;

import com.assignment.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserQueryDslRepository {}
