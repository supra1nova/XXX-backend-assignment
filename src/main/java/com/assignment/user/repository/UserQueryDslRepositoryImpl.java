package com.assignment.user.repository;

import com.assignment.user.entity.QUser;
import com.assignment.user.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QUser qUser = QUser.user;

    @Override
    public Page<User> searchUsers(String userName, Integer userAge, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(userName)) {
            builder.and(qUser.userName.contains(userName));
        }
        if (userAge != null) {
            builder.and(qUser.userAge.eq(userAge));
        }

        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order sortOrder : pageable.getSort()) {
            Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;
            String property = sortOrder.getProperty();

            switch (property) {
                case "userId" -> orders.add(new OrderSpecifier<>(direction, qUser.userId));
                case "userName" -> orders.add(new OrderSpecifier<>(direction, qUser.userName));
                case "updatedAt" -> orders.add(new OrderSpecifier<>(direction, qUser.updatedAt));
                default -> orders.add(new OrderSpecifier<>(direction, qUser.createdAt));
            }
        }

        List<User> results = queryFactory
            .selectFrom(qUser)
            .where(builder)
            .orderBy(orders.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qUser.count())
            .from(qUser)
            .where(builder)
            .fetchOne();

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            () -> Objects.requireNonNullElse(total, 0L)
        );
    }
}
