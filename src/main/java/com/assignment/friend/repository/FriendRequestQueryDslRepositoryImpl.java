package com.assignment.friend.repository;

import com.assignment.common.model.Cursorable;
import com.assignment.friend.dto.FriendRequestListResponseDto;
import com.assignment.friend.entity.QFriendRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendRequestQueryDslRepositoryImpl implements FriendRequestQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QFriendRequest qFriendRequest = QFriendRequest.friendRequest;

    @Override
    public List<FriendRequestListResponseDto> searchFriendRequests(Long userId, Long fromUserId, Cursorable cursorable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qFriendRequest.toUser.userId.eq(userId));

        if (fromUserId != null) {
            builder.and(qFriendRequest.fromUser.userId.eq(fromUserId));
        }

        Duration window = cursorable.getWindowDuration();
        if (window != null) {
            Instant threshold = Instant.now().minus(window);
            builder.and(qFriendRequest.requestedAt.goe(threshold));
        }

        String requestId = cursorable.getRequestId();
        if (StringUtils.isNotBlank(requestId)) {
            builder.and(qFriendRequest.requestId.eq(requestId));
        }

        List<OrderSpecifier<?>> orders = List.of(new OrderSpecifier<>(
            cursorable.getDirection(),
            qFriendRequest.requestedAt
        ));

        return queryFactory
            .select(Projections.constructor(
                FriendRequestListResponseDto.class,
                qFriendRequest.requestId,
                qFriendRequest.fromUser.userId.as("fromUserId"),
                qFriendRequest.requestedAt
            ))
            .from(qFriendRequest)
            .where(builder)
            .orderBy(orders.toArray(new OrderSpecifier[0]))
            .limit(cursorable.getMaxSize())
            .fetch();
    }
}
