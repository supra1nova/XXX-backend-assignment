package com.assignment.friend.service;

import com.assignment.common.model.PageResponseDto;
import com.assignment.friend.dto.FriendRequestHistoryListRequestDto;
import com.assignment.friend.dto.FriendRequestHistoryListResponseDto;
import com.assignment.friend.entity.FriendRequestHistory;
import com.assignment.friend.repository.FriendRequestHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendRequestHistoryService {
    private final FriendRequestHistoryRepository friendRequestHistoryRepository;

    public PageResponseDto<FriendRequestHistoryListResponseDto> findFriendRequestHistoryList(FriendRequestHistoryListRequestDto requestDto) {
        requestDto.init();
        Pageable pageable = requestDto.toPageable(requestDto.getSort());

        Page<FriendRequestHistory> pageUserList = friendRequestHistoryRepository.searchHistory(pageable);

        Page<FriendRequestHistoryListResponseDto> friendRequestHistoryListResponseDto = pageUserList.map(FriendRequestHistory::toFriendRequestHistoryListResponseDto);
        return PageResponseDto.from(friendRequestHistoryListResponseDto);
    }
}
