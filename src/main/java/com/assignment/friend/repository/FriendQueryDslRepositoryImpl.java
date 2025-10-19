package com.assignment.friend.repository;

import com.assignment.friend.dto.FriendListResponseDto;
import com.assignment.friend.entity.QFriend;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        BooleanBuilder builder = new BooleanBuilder();

        if (userId != null) {
            builder.and(qFriend.user.userId.eq(userId));
            builder.and(
                qFriend.toUser.userId.eq(userId)
                    .or(qFriend.fromUser.userId.eq(userId))
            );
        }
        if (fromUserId != null) {
            builder.and(qFriend.fromUser.userId.eq(fromUserId));
        }
        if (toUserId != null) {
            builder.and(qFriend.toUser.userId.eq(toUserId));
        }

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order sortOrder : pageable.getSort()) {
            Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;
            String property = sortOrder.getProperty();

            switch (property) {
                case "userId" -> orders.add(new OrderSpecifier<>(direction, qFriend.user.userId));
                case "fromUserId" ->
                    orders.add(new OrderSpecifier<>(direction, qFriend.fromUser.userId));
                case "toUserId" ->
                    orders.add(new OrderSpecifier<>(direction, qFriend.toUser.userId));
                default -> orders.add(new OrderSpecifier<>(direction, qFriend.approvedAt));
            }
        }

        List<FriendListResponseDto> content = queryFactory
            .select(Projections.constructor(FriendListResponseDto.class,
                qFriend.user.userId,
                qFriend.fromUser.userId,
                qFriend.toUser.userId,
                qFriend.approvedAt)
            )
            .from(qFriend)
            .where(builder)
            .orderBy(orders.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qFriend.count())
            .from(qFriend)
            .where(builder)
            .fetchOne();

        return PageableExecutionUtils.getPage(
            content,
            pageable,
            () -> Objects.requireNonNullElse(total, 0L)
        );
    }
}
