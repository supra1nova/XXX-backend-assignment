package com.assignment.common.webclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestApiService {
    private final static String URI = "/friends/request";
    private final static String X_USER_ID = "X-USER-ID";
    private final static String FROM_USER_ID = "2";
    private final static String SUCCESS_MESSAGE = "✅ 요청 {} 완료 {}";
    private final static String FAIL_MESSAGE = "❌ 요청 {} 실패 {}";
    private final static String ALL_SUCCESS_MESSAGE = "🔥 모든 요청 완료";

    private final WebClient webClient;

    public void postCreateFriendRequestTest() {
        //@formatter:off
        String json = """
            {
              "toUserId": "20"
            }
            """;

        // 1~20까지 20개의 요청을 동시에 실행
        List<CompletableFuture<String>> futures = IntStream.rangeClosed(1, 20)
            .mapToObj(i -> webClient.post()
                .uri(URI)
                .header(X_USER_ID, FROM_USER_ID)
                .bodyValue(json)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(res -> log.info(SUCCESS_MESSAGE, i, res))
                .doOnError(err -> log.info(FAIL_MESSAGE, i, err.getMessage()))
                .toFuture()
            )
            .toList();

        // 모든 요청 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info(ALL_SUCCESS_MESSAGE);
    }
}
