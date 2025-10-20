package com.assignment.domain.friend.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FriendId implements Serializable {
    private Long userId;
    private Long fromUserId;
    private Long toUserId;
}
