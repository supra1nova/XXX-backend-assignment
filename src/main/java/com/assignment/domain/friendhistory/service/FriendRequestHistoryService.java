package com.assignment.domain.friendhistory.service;

import com.assignment.domain.friendhistory.dto.FriendRequestHistoryListRequestDto;
import com.assignment.domain.friendhistory.dto.FriendRequestHistoryListResponseDto;
import com.assignment.domain.friendhistory.entity.FriendRequestHistory;
import com.assignment.domain.friendhistory.repository.FriendRequestHistoryRepository;
import com.assignment.common.model.PageResponseDto;
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
