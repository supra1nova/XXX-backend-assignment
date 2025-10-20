package com.assignment.common.init;

import com.assignment.domain.friend.entity.Friend;
import com.assignment.domain.user.entity.User;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Faker faker = new Faker(Locale.KOREA);

    // FIXME: 더 많은 모수가 필요한 경우 변경
    //    private static final int USER_COUNT = 1_000_000;
    private static final int USER_COUNT = 1_000;
    private static final int BATCH_SIZE = 5_000;

    @Override
    public void run(String... args) {
        log.info("Dummy data initialization started");

        processCreateUsers();

        List<User> users = jdbcTemplate.query("""
                SELECT user_id, user_name, user_age 
                FROM tb_user
                WHERE user_id > 20
                """,
            (rs, rowNum) -> User.restore(
                rs.getLong("user_id"),
                rs.getString("user_name"),
                rs.getInt("user_age")
            )
        );

        processBatchInsertFriend(users);
    }

    private void processCreateUsers() {
        log.info("--- User data generation started !! ---");
        long startMs = System.currentTimeMillis();

        int block = USER_COUNT / 4;

        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> processBatchInsertUsers(0, block * 1), executor);
        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> processBatchInsertUsers(block * 1, block * 2), executor);
        CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> processBatchInsertUsers(block * 2, block * 3), executor);
        CompletableFuture<Void> f4 = CompletableFuture.runAsync(() -> processBatchInsertUsers(block * 3, block * 4), executor);

        // 모든 작업이 끝날 때까지 기다림
        CompletableFuture.allOf(f1, f2, f3, f4).join();

        long endMs = System.currentTimeMillis();
        log.info("--- ✅ User data generation succeeded in {} ms !! ---", (endMs - startMs));
    }

    private void processBatchInsertUsers(Integer start, Integer end) {
        List<User> buffer = new ArrayList<>(BATCH_SIZE);

        for (int i = start; i < end; i++) {
            String name = faker.name().fullName().replace(" ", "") + i;
            int age = faker.number().numberBetween(18, 65);
            User user = User.create(name, age);
            buffer.add(user);

            if (buffer.size() >= BATCH_SIZE) {
                batchInsertUsers(buffer);
                buffer.clear();
            }
        }

        // 마지막 남은 데이터 flush
        if (!buffer.isEmpty()) {
            batchInsertUsers(buffer);
        }
    }

    private void batchInsertUsers(List<User> users) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO tb_user (user_id, user_name, user_age, created_at) VALUES (NEXT VALUE FOR user_seq, ?, ?, ?)",
            users,
            BATCH_SIZE,
            (ps, user) -> {
                ps.setString(1, user.getUserName());
                ps.setInt(2, user.getUserAge());
                ps.setTimestamp(3, Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MILLIS)));
            }
        );
    }

    private void processBatchInsertFriend(List<User> users) {
        // Friend 관계 생성 (각 페어당 로우 2개)
        List<Friend> buffer = new ArrayList<>(BATCH_SIZE);
        Set<String> createdPairs = new HashSet<>();

        int targetPairs = users.size() * 2;
        int attempts = 0;
        long start = System.currentTimeMillis();

        while (createdPairs.size() < targetPairs && attempts < (users.size() / 2)) {
            attempts++;

            User a = users.get(ThreadLocalRandom.current().nextInt(users.size()));
            User b = users.get(ThreadLocalRandom.current().nextInt(users.size()));
            if (a.equals(b)) continue;

            long min = Math.min(a.getUserId(), b.getUserId());
            long max = Math.max(a.getUserId(), b.getUserId());
            String key = min + ":" + max;
            if (createdPairs.contains(key)) continue;

            // 요청자 랜덤 선택
            User from = faker.bool().bool() ? a : b;
            User to = from.equals(a) ? b : a;

            // 로우 1: 요청자 입장에서
            buffer.add(Friend.create(from, from, to));
            // 로우 2: 수신자 입장에서
            buffer.add(Friend.create(to, from, to));

            createdPairs.add(key);

            // 버퍼가 차면 DB에 insert
            if (buffer.size() >= BATCH_SIZE) {
                batchInsertFriends(buffer);
                buffer.clear();
            }
        }

        // 마지막 남은 데이터 flush
        if (!buffer.isEmpty()) {
            batchInsertFriends(buffer);
        }

        long end = System.currentTimeMillis();
        log.info("✅ Inserted {} friend rows in {} ms", createdPairs.size() * 2, (end - start));
    }

    private void batchInsertFriends(List<Friend> friends) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO tb_friend (user_id, from_user_id, to_user_id, approved_at) VALUES (?, ?, ?, ?)",
            friends,
            BATCH_SIZE,
            (ps, friend) -> {
                ps.setLong(1, friend.getUser().getUserId());
                ps.setLong(2, friend.getFromUser().getUserId());
                ps.setLong(3, friend.getToUser().getUserId());
                ps.setTimestamp(4, Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MILLIS)));
            }
        );
    }

    // ✅ Spring 컨텍스트 종료 시 자동 실행됨
    @PreDestroy
    public void shutdownExecutor() {
        log.info("ExecutorService 종료 중...");
        executor.shutdown();
    }
}

