package com.assignment.friend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendRequestStatus {
    PENDING("대기중"),
    ACCEPTED("승인완료"),
    REJECTED("거절"),
    ;

    private final String desc;
}
