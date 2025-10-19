package com.assignment.friend.entity;

import com.assignment.friend.dto.FriendListResponseDto;
import com.assignment.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table(
    name = "friends",
    indexes = {
        @Index(name = "idx_friends_user_id", columnList = "user_id"),
        @Index(name = "idx_friends_from_to", columnList = "from_user_id, to_user_id"),
        @Index(name = "idx_friends_to_from", columnList = "to_user_id, from_user_id"),
        @Index(name = "idx_friends_user_from", columnList = "user_id, from_user_id"),
        @Index(name = "idx_friends_user_to", columnList = "user_id, to_user_id"),
        @Index(name = "idx_friends_from_user", columnList = "from_user_id"),
        @Index(name = "idx_friends_to_user", columnList = "to_user_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Friend {
    @EmbeddedId
    private FriendId friendId;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("fromUserId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @MapsId("toUserId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @CreatedDate
    @Column(name = "approved_at", nullable = false)
    private Instant approvedAt;

    public FriendListResponseDto toFriendListResponseDto() {
        return FriendListResponseDto.builder()
            .userId(user.getUserId())
            .fromUserId(fromUser.getUserId())
            .toUserId(toUser.getUserId())
            .approvedAt(approvedAt)
            .build();
    }
}
