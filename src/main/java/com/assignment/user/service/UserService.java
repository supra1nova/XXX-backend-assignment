package com.assignment.user.service;

import com.assignment.common.model.PageResponseDto;
import com.assignment.user.dto.UserListResponseDto;
import com.assignment.user.dto.UserListRequestDto;
import com.assignment.user.entity.User;
import com.assignment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public PageResponseDto<UserListResponseDto> findUserList(UserListRequestDto requestDto) {
        requestDto.init();
        Pageable pageable = requestDto.toPageable(requestDto.getSort());

        Page<User> pageUserList = userRepository.searchUsers(
            requestDto.getName(),
            requestDto.getAge(),
            pageable
        );

        Page<UserListResponseDto> responseUserList = pageUserList.map(User::toUserListResponseDto);
        return PageResponseDto.from(responseUserList);
    }
}
