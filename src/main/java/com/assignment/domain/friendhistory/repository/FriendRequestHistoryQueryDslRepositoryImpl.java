package com.assignment.domain.friendhistory.repository;

import com.assignment.domain.friendhistory.entity.FriendRequestHistory;
import com.assignment.domain.friendhistory.entity.QFriendRequestHistory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class FriendRequestHistoryQueryDslRepositoryImpl implements FriendRequestHistoryQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QFriendRequestHistory qFriendRequestHistory = QFriendRequestHistory.friendRequestHistory;

    @Override
    public Page<FriendRequestHistory> searchHistory(Pageable pageable) {
        BooleanBuilder condition = new BooleanBuilder();

        Sort.Order sortOrder = pageable.getSort().stream().findFirst()
            .orElse(new Sort.Order(Sort.Direction.DESC, "createdAt"));

        Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(direction, qFriendRequestHistory.createdAt);

        List<FriendRequestHistory> results = queryFactory
            .selectFrom(qFriendRequestHistory)
            .orderBy(orderSpecifier)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qFriendRequestHistory.count())
            .from(qFriendRequestHistory)
            .where(condition)
            .fetchOne();

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            () -> Objects.requireNonNullElse(total, 0L)
        );
    }
}
