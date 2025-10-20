package com.assignment.domain.friendrequest.entity;

import com.assignment.common.utils.RandomIdGenerator;
import com.assignment.domain.user.entity.User;
import io.micrometer.common.util.StringUtils;
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
    name = "tb_friend_request",
    indexes = {
        @Index(name = "idx_friend_request_from", columnList = "from_user_id"),
        @Index(name = "idx_friend_request_to", columnList = "to_user_id"),
        @Index(name = "idx_friend_request_from_to", columnList = "from_user_id, to_user_id"),
        @Index(name = "idx_friend_request_to_from", columnList = "to_user_id, from_user_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class FriendRequest {
    @Id
    @Column(name = "request_id")
    private String requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @CreatedDate
    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @PrePersist
    public void prePersist() {
        if (StringUtils.isBlank(requestId)) {
            requestId = RandomIdGenerator.epochUuid();
        }
    }

    public static FriendRequest create(User fromUser, User toUser) {
        return FriendRequest.builder()
            .fromUser(fromUser)
            .toUser(toUser)
            .build();
    }
}
