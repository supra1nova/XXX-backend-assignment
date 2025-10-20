package com.assignment.domain.friend.entity;

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
    name = "tb_friend",
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
@EntityListeners(AuditingEntityListener.class)
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

    public static Friend create(User user, User fromUser, User toUser) {
        if (!user.equals(fromUser) && !user.equals(toUser)) {
            throw new IllegalArgumentException("user는 fromUser 또는 toUser와 동일해야 합니다.");
        }
        return Friend.builder()
            .friendId(new FriendId(user.getUserId(), fromUser.getUserId(), toUser.getUserId()))
            .user(user)
            .fromUser(fromUser)
            .toUser(toUser)
            .build();
    }
}
