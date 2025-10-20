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

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QUser qUser = QUser.user;

    @Override
    public Page<User> searchUsers(String userName, Integer userAge, Pageable pageable) {
        BooleanBuilder condition = new BooleanBuilder();

        if (StringUtils.isNotBlank(userName)) {
            condition.and(qUser.userName.contains(userName));
        }
        if (userAge != null) {
            condition.and(qUser.userAge.eq(userAge));
        }

        Sort.Order sortOrder = pageable.getSort().stream().findFirst()
            .orElse(new Sort.Order(Sort.Direction.DESC, "createdAt"));

        Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;
        String property = sortOrder.getProperty();

        OrderSpecifier<?> orderSpecifier = switch (property) {
            case "userId" -> new OrderSpecifier<>(direction, qUser.userId);
            case "userName" -> new OrderSpecifier<>(direction, qUser.userName);
            case "updatedAt" -> new OrderSpecifier<>(direction, qUser.updatedAt);
            default -> new OrderSpecifier<>(direction, qUser.createdAt);
        };

        List<User> results = queryFactory
            .selectFrom(qUser)
            .where(condition)
            .orderBy(orderSpecifier)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qUser.count())
            .from(qUser)
            .where(condition)
            .fetchOne();

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            () -> Objects.requireNonNullElse(total, 0L)
        );
    }
}
