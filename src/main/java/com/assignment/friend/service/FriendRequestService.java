package com.assignment.friend.service;

import com.assignment.common.model.CursorPageResponseDto;
import com.assignment.common.model.Cursorable;
import com.assignment.friend.dto.FriendRequestListRequestDto;
import com.assignment.friend.dto.FriendRequestListResponseDto;
import com.assignment.friend.repository.FriendRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;

    public CursorPageResponseDto<FriendRequestListResponseDto> selectFriendRequestList(
        Long userId,
        FriendRequestListRequestDto requestDto
    ) {
        requestDto.init();
        Cursorable cursorable = requestDto.toCursorable(requestDto.getSort());

        List<FriendRequestListResponseDto> friendRequestList = friendRequestRepository.searchFriendRequests(
            userId,
            requestDto.getRequestUserId(),
            cursorable
        );

        return CursorPageResponseDto.of(friendRequestList, requestDto);
    }
}
