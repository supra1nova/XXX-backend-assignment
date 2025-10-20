package com.assignment.domain.friendrequest.repository;

import com.assignment.common.model.Cursorable;
import com.assignment.domain.friendrequest.dto.FriendRequestListResponseDto;
import com.assignment.domain.friendrequest.entity.FriendRequest;
import com.assignment.domain.friendrequest.entity.QFriendRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendRequestQueryDslRepositoryImpl implements FriendRequestQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QFriendRequest qFriendRequest = QFriendRequest.friendRequest;

    @Override
    public List<FriendRequestListResponseDto> searchFriendRequests(Long userId, Long fromUserId, Cursorable cursorable) {
        BooleanBuilder condition = new BooleanBuilder();

        condition.and(qFriendRequest.toUser.userId.eq(userId));

        if (fromUserId != null) {
            condition.and(qFriendRequest.fromUser.userId.eq(fromUserId));
        }

        Duration window = cursorable.getWindowDuration();
        if (window != null) {
            Instant threshold = Instant.now().minus(window);
            condition.and(qFriendRequest.requestedAt.goe(threshold));
        }

        String requestId = cursorable.getRequestId();
        if (StringUtils.isNotBlank(requestId)) {
            condition.and(qFriendRequest.requestId.eq(requestId));
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
            .where(condition)
            .orderBy(orders.toArray(new OrderSpecifier[0]))
            .limit(cursorable.getMaxSize())
            .fetch();
    }

    @Override
    public Optional<FriendRequest> searchFriendRequestByFromAndToUserIds(Long fromUserId, Long toUserId) {
        QFriendRequest friendRequest = QFriendRequest.friendRequest;

        BooleanBuilder condition = new BooleanBuilder();
        BooleanExpression doesFromUserRequest = friendRequest.fromUser.userId.eq(fromUserId)
            .and(friendRequest.toUser.userId.eq(toUserId));
        BooleanExpression doesToUserRequest = friendRequest.fromUser.userId.eq(toUserId)
            .and(friendRequest.toUser.userId.eq(fromUserId));
        condition.andAnyOf(doesFromUserRequest, doesToUserRequest);

        FriendRequest result = queryFactory
            .selectFrom(friendRequest)
            .where(condition)
            .fetchFirst();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<FriendRequest> searchFriendRequestByAllIds(String requestId, Long fromUserId, Long toUserId) {
        QFriendRequest friendRequest = QFriendRequest.friendRequest;

        FriendRequest result = queryFactory
            .selectFrom(friendRequest)
            .where(
                friendRequest.requestId.eq(requestId),
                friendRequest.fromUser.userId.eq(fromUserId),
                friendRequest.toUser.userId.eq(toUserId)
            )
            .fetchFirst();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<FriendRequest> searchFriendRequestByRequestAndToUserIds(String requestId, Long toUserId) {
        QFriendRequest friendRequest = QFriendRequest.friendRequest;

        FriendRequest result = queryFactory
            .selectFrom(friendRequest)
            .where(
                friendRequest.requestId.eq(requestId),
                friendRequest.toUser.userId.eq(toUserId)
            )
            .fetchFirst();
        return Optional.ofNullable(result);
    }
}
