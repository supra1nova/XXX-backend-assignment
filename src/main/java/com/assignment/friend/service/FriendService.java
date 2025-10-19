package com.assignment.friend.service;

import com.assignment.common.model.PageResponseDto;
import com.assignment.friend.dto.FriendListRequestDto;
import com.assignment.friend.dto.FriendListResponseDto;
import com.assignment.friend.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;

    public PageResponseDto<FriendListResponseDto> selectFriendList(
        Long userId,
        FriendListRequestDto requestDto
    ) {
        requestDto.init();
        Pageable pageable = requestDto.toPageable(requestDto.getSort());

        Page<FriendListResponseDto> responseFriendListPage = friendRepository.searchFriends(
            userId,
            requestDto.getFromUserId(),
            requestDto.getToUserId(),
            pageable
        );

        return PageResponseDto.from(responseFriendListPage);
    }
}
