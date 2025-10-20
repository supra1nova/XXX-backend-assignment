package com.assignment.domain.friendhistory.entity;

import com.assignment.domain.friendrequest.entity.FriendRequest;
import com.assignment.domain.friendhistory.dto.FriendRequestHistoryListResponseDto;
import com.assignment.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Table(
    name = "friend_requests_history",
    indexes = {
        @Index(name = "idx_friend_request_history_from", columnList = "from_user_id"),
        @Index(name = "idx_friend_request_history_to", columnList = "to_user_id"),
        @Index(name = "idx_friend_request_history_from_to", columnList = "from_user_id, to_user_id"),
        @Index(name = "idx_friend_request_history_to_from", columnList = "to_user_id, from_user_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class FriendRequestHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id")
    private String historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "request_id", nullable = false,
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private FriendRequest friendRequest;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public FriendRequestHistoryListResponseDto toFriendRequestHistoryListResponseDto() {
        return FriendRequestHistoryListResponseDto.builder()
            .historyId(historyId)
            .requestId(friendRequest.getRequestId())
            .fromUserId(fromUser.getUserId())
            .toUserId(toUser.getUserId())
            .createdAt(createdAt)
            .status(status)
            .build();
    }

    public static FriendRequestHistory create(
        FriendRequest friendRequest,
        User fromUser,
        User toUser,
        FriendRequestStatus status
    ) {
        return FriendRequestHistory.builder()
            .friendRequest(friendRequest)
            .fromUser(fromUser)
            .toUser(toUser)
            .status(status)
            .build();
    }
}
