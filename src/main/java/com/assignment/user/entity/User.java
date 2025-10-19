package com.assignment.user.entity;

import com.assignment.common.model.UserListResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@SequenceGenerator(
    name = "user_seq_gen",
    sequenceName = "user_seq",
    allocationSize = 1
)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "user_age", nullable = false, columnDefinition = "INT DEFAULT 20")
    private Integer userAge;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    public UserListResponseDto toUserListResponseDto() {
        return UserListResponseDto.builder()
            .userId(userId)
            .userName(userName)
            .userAge(userAge)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }
}
