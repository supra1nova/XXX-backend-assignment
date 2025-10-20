package com.assignment.friend.repository;

import com.assignment.friend.dto.FriendListResponseDto;
import com.assignment.friend.entity.QFriend;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendQueryDslRepositoryImpl implements FriendQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QFriend qFriend = QFriend.friend;

    @Override
    public Page<FriendListResponseDto> searchFriends(Long userId, Long fromUserId, Long toUserId, Pageable pageable) {
        BooleanBuilder condition = new BooleanBuilder();

        if (userId != null) {
            condition.and(qFriend.user.userId.eq(userId));
            condition.and(
                qFriend.toUser.userId.eq(userId)
                    .or(qFriend.fromUser.userId.eq(userId))
            );
        }
        if (fromUserId != null) {
            condition.and(qFriend.fromUser.userId.eq(fromUserId));
        }
        if (toUserId != null) {
            condition.and(qFriend.toUser.userId.eq(toUserId));
        }

        Sort.Order sortOrder = pageable.getSort().stream().findFirst()
            .orElse(new Sort.Order(Sort.Direction.DESC, "approvedAt"));

        Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;
        String property = sortOrder.getProperty();

        OrderSpecifier<?> orderSpecifier = switch (property) {
            case "userId" -> new OrderSpecifier<>(direction, qFriend.user.userId);
            case "fromUserId" -> new OrderSpecifier<>(direction, qFriend.fromUser.userId);
            case "toUserId" -> new OrderSpecifier<>(direction, qFriend.toUser.userId);
            default -> new OrderSpecifier<>(direction, qFriend.approvedAt);
        };

        List<FriendListResponseDto> content = queryFactory
            .select(Projections.constructor(FriendListResponseDto.class,
                qFriend.user.userId,
                qFriend.fromUser.userId,
                qFriend.toUser.userId,
                qFriend.approvedAt)
            )
            .from(qFriend)
            .where(condition)
            .orderBy(orderSpecifier)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qFriend.count())
            .from(qFriend)
            .where(condition)
            .fetchOne();

        return PageableExecutionUtils.getPage(
            content,
            pageable,
            () -> Objects.requireNonNullElse(total, 0L)
        );
    }

    @Override
    public boolean existsFriendByFromAndToUserId(Long fromUserId, Long toUserId) {
        BooleanBuilder condition = new BooleanBuilder();

        BooleanExpression doesFromUserRequest = qFriend.fromUser.userId.eq(fromUserId)
            .and(qFriend.toUser.userId.eq(toUserId));
        BooleanExpression doesToUserRequest = qFriend.fromUser.userId.eq(toUserId)
            .and(qFriend.toUser.userId.eq(fromUserId));

        condition.andAnyOf(doesFromUserRequest, doesToUserRequest);

        return queryFactory
            .selectOne()
            .from(qFriend)
            .where(condition)
            .fetchFirst() != null;
    }
}
