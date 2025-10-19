package com.assignment.friend.entity;

import com.assignment.common.utils.RandomIdGenerator;
import com.assignment.friend.dto.FriendRequestListResponseDto;
import com.assignment.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue
    @Column(name = "request_id")
    private String requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recieve_user_id", nullable = false)
    private User toUser;

    @CreatedDate
    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @PrePersist
    public void prePersist() {
        if (requestId == null) {
            requestId = RandomIdGenerator.epochUuid();
        }
    }

    public FriendRequestListResponseDto toFriendListResponseDto() {
        return FriendRequestListResponseDto.builder()
            .requestId(requestId)
            .fromUserId(fromUser.getUserId())
            .requestedAt(requestedAt)
            .build();
    }
}
