# apr-backend-assignment / 에이피알 백엔드 직무과제
SpringBoot 3.x.x / H2 (in memory) / Spring Data JPA / Gradle / JAVA 21

---

## 📋 개요 및 목표

- 친구 목록 / 친구 신청 목록/친구 신청 / 친구 신청 승인 / 친구 신청 거절 등 기능 구현
- 도구 제한사항 없으며 ai 활용 가능
- 요구 사항 미포함 내용 및 불명확한 로직 임의 보완 후 근거 제시 필수
- API 명세 Swagger로 확인 가능하도록 구성
- feature 브랜치 작업 -> develop 브랜치 병합 -> 최종 작업 완료시 main 브랜치 병합
- Readme.md 내 실행 방법 및 핵심 로직 설명 기술 필수

---

## 📝 구현 방향

- 구현 흐름은 git init -> project init -> 기본 공통 모듈 구축 -> 도메인 별 기능 구축 -> 기타 추가 필수 사항 구현 등 순으로 작업
- 유저 / 친구 / 친구 신청 / 친구 신청 내역 등의 도메인 추출 및 관련 기능 구현
- 친구 목록과 친구 신청 목록의 response 형태 고려해 일반형/커서형 페이지네이션 기능 분할 필요시 관련 DTO에 상속 적용
- 1분 10회 제한 관련 rate-limit 기능 및 분산락 기능 적용
- 문서화 관련 swagger 적용
- ui 테스트 가능한 테스트용 데이터 구축

---

## 📋 사용 도구 및 라이브러리

- spring-boot-starter-web
- spring-boot-configuration-processor
- spring-boot-devtools
- spring-data-jpa
- spring-boot-starter-data-redis
- spring-boot-starter-webflux
- springdoc-openapi-starter-webmvc-ui
- querydsl-jpa
- h2:2.3.232
- log4j2
- lombok
- common-lang3
- uuid
- tsid
- datafaker

---

## ⚠️ 구동 위한 사전 필요 사항

- 구동 환경 내 redis 설치 필수 (localhost:6379) 
  - 다른 설정인 경우 application.yml 의 spring.data.redis.host/port 변경 요망
- application.yml 을 .gitignore 해제 후 push 했음에 따라 바로 구동 가능

---

### 초기 데이터 구성

- ddl 을 src/main/resources/db/schema.sql 에 구성했고, 동일한 경로의 data.sql 파일에는 테스트용 dml을 구성했습니다.
  - 1번 부터 20번 유저까지 고정된 값으로 유저가 생성됩니다.
  - 1번 유저는 2번부터 10번 유저까지 친구입니다.
  - 2번 유저는 1번 유저와 그리고 3번부터 6번 유저까지 친구입니다.
  - 10번 부터 19번 유저가 2번 유저에게 친구 신청을 한 상황입니다.
  - 6번 부터 15번 유저가 3번 유저에게 친구 신청을 한 상황입니다.
  - 1번 유저가 11번 부터 16번 유저에게 친구 신청을 한 상황입니다.
- 프로젝트 구동시 spring batch 가 추가 더미 유저 1,000명과 친구 관계를 랜덤하게 구성합니다. 
  - 수동으로 생성된 1번 부터 20번 유저에게는 영향을 미치지 않습니다. 

---

## 🪧 주요 로직 설명

### 1.Log 관련 logback + LogInterceptor 적용

- MOC 적용으로 Interceptor 에서 TSID로 생성된 traceId를 logback에 출력 가능
- traceId 기준 요청 단위별 로그 추적, 요청 처리 완료 후 MOC에 저장된 ID 제거
    ```java
    public class LogInterceptor implements HandlerInterceptor {
    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        ...
    
        @Override
        public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
            String traceId = RandomIdGenerator.tsid();
            MDC.put(TRACE_ID_KEY, traceId);
            ...
            if (handler instanceof HandlerMethod handlerMethod) {
                String controllerName = handlerMethod.getBeanType().getSimpleName();
                String methodName = handlerMethod.getMethod().getName();
    
                log.info("[REQ] [{}#{}] {}  {}{}  ip={}", controllerName, methodName, method, uri, query, ip);
            } else {
                log.info("[REQ] {}  {}{}  ip={}", method, uri, query, ip);
            }
        }
        
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            MDC.remove(TRACE_ID_KEY);
        }
    }
    ```

    ```xml
        ...
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
                <pattern>%d{HH:mm:ss.SSS} [%-5level][%-19thread][%X{traceId:-SYSTEM}][%file:%line] - %msg%n</pattern>
            </encoder>
        </appender>
        ...

    ```

---

### 2.응답 객체 일원화

- 공통 응답 객체 사용으로 클라이언트가 받는 응답 일원화 시도
- timestamp / code / message 등 과 커스텀 응답 코드를 이용해 처리 결과 안내
- 다만 친구 목록/친구 신청 목록 등과 관련해 과제에서 주어진 response 와 상이한 관계로, 목록 로직만 ResponseBody 에서
  timestamp/code/message 가 제거된 {data: data} 형태로 ResponseEntity body 에 넣어 리턴하도록 처리
    ```java
    public class ResponseBody {
        @Schema(description = "응답 래퍼용 객체")
        private String timestamp;
        @Schema(description = "응답 코드")
        private String code;
        @Schema(description = "응답 메세지")
        private String message;
    
        public static ResponseEntity<ResponseBody> toResponseEntity(ResponseCode responseCode) {
            log.info("[toResponse] {}, {}, {}", responseCode.getHttpStatus(), responseCode.getCode(), responseCode.getDetail());
            return ResponseEntity
                .status(responseCode.getHttpStatus())
                .body(ResponseBody.builder()
                    .timestamp(CommonUtils.toCustomDateTimeString(Instant.now()))
                    .code(responseCode.getCode())
                    .message(responseCode.getDetail())
                    .build()
                );
        }
    
        public static <T> ResponseEntity<T> toResponseEntity(ResponseCode responseCode, T data) {
            log.info("[toResponse] {}, {}, {}, {}", responseCode.getHttpStatus(), responseCode.getCode(), responseCode.getDetail(), data);
            return ResponseEntity
                .status(responseCode.getHttpStatus())
                .body(data);
        }
    
        public static ResponseEntity<ResponseBody> toErrorResponseEntity(ResponseCode responseCode, Exception e) {
            log.error("[toErrorResponse] {}, {}", responseCode.getHttpStatus(), responseCode.getCode());
            return ResponseEntity
                .status(responseCode.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResponseBody.builder()
                    .timestamp(CommonUtils.toCustomDateTimeString(Instant.now()))
                    .code(responseCode.getCode())
                    .message(responseCode.getDetail())
                    .build()
                );
        }
    }
    ```

---

---

### 3.커스텀 응답 코드 활용

- API 응답 코드와 메시지 표준화
- 컨트롤러 및 예외처리 등 전역적으로 동일한 코드/메세지 포멧 사용 가능
- HTTP 상태코드 및 내부 비지니스 코드 그리고 메세지를 함께 관리함으로써 공통 응답 규약 확립
    ```java
        public enum ResponseCode {
            /* 200 */
            OK(HttpStatus.OK, "S00001", "성공적으로 처리되었습니다."),
            ...
            /* 400 */
            INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E00001", "잘못된 요청입니다."),
            ...
        }
    ```

---

### 4.Transaction aspect

- @Transactionalal 없이 트랜젝션 전역 적용 추구
- 읽기/쓰기 트랜잭션 구분 통해 서비스 단의 호출 메서드명에 따른 자동 적용
- 메서드명 상이해도 사용자 임의 @Transactionalal 추가시 호환 가능
    ```java
    public class TransactionAspect {
        private static final int TX_READ_METHOD_TIMEOUT = 30 * 60; // in seconds
        private static final int TX_WRITE_METHOD_TIMEOUT = 30 * 60; // in seconds
        private static final String AOP_TX_WRITE_EXP = """
                   execution(* com.peter.gradletest..service..*Service.insert*(..)) "
                || execution(* com.peter.gradletest..service..*Service.update*(..))
                || execution(* com.peter.gradletest..service..*Service.delete*(..))
                || execution(* com.peter.gradletest..service..*Service.save*(..))
                || execution(* com.peter.gradletest..service..*Service.process*(..))
            """;
        
        private static final String AOP_TX_READ_EXP = " (execution(* com.peter.gradletest..service..*Service.*(..)) "
            + "||  execution(* com.peter.gradletest..service..*Service.*(..))) "
            + "&& !(" + AOP_TX_WRITE_EXP + ") ";
        ...
    
        @Around("transactionReadPointcut() || transactionWritePointcut()")
        public Object transactionMethod(ProceedingJoinPoint joinPoint) throws Throwable {
            TransactionDefinition transactionDefinition = createTransactionDefinition(joinPoint);
            ...
        }
        
        ...
    
        private TransactionDefinition createTransactionDefinition(ProceedingJoinPoint joinPoint) {
            DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    
            if (isReadMethod(joinPoint)) {
                transactionDefinition.setReadOnly(true);
                transactionDefinition.setTimeout(TX_READ_METHOD_TIMEOUT);
            } else {
                transactionDefinition.setTimeout(TX_WRITE_METHOD_TIMEOUT);
            }
    
            return transactionDefinition;
        }
        
        ...
    }
    ```

---

### 5.ExceptionAdvice

- 발생 가능한 언체크드 계열 예외, 기타 로직상 고의로 발생시킨 모든 CustomException 등 예외 핸들링
- 핸들링된 에러는 공통적으로 resp() 를 호출하며, ResponseBody.toErrorResponseEntity 통해 에러 메세지로 변환 및 클라이언트단으로 리턴
    ```java
    public class ExceptionAdvice {
        @ExceptionHandler(CustomException.class)
        protected Object handleCustomException(HttpServletRequest req, CustomException e) {
            CommonUtils.printRequestObject(req);
            ExceptionUtils.printException("CustomException", req, e);
            return resp(e.getResponseCode(), e);
        }
    
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public Object handleMethodArgumentTypeMismatchException(HttpServletRequest req, MethodArgumentTypeMismatchException e) {
                CommonUtils.printRequestObject(req);
                ExceptionUtils.printException("MethodArgumentTypeMismatchException", req, e);
                return resp(ResponseCode.INVALID_TYPE_ERROR, e);
        }
        
        @ExceptionHandler(Exception.class)
        protected Object handleException(HttpServletRequest req, Exception e) {
            ExceptionUtils.printException("Exception", req, e);
            return resp(ResponseCode.INTERNAL_SERVER_ERROR, e);
        }
        
        ...
        
        protected Object resp(ResponseCode code, Exception e) {
            return ResponseBody.toErrorResponseEntity(code, e);
        }
    }
    ```

---

### 6.친구 목록, 친구 신청 목록 그리고 일반/커서 페이지네이션

- 각 목록 조회시 각 도메인별 Dto 내 name, age, sort 값이 담겨서 요청되며, dto 내에 정의된 init() 로직이 서비스 단에서 가장 먼저 실행되어 페이징 관련 기본 속성과 개별 sort 값을 초기화 (빈 값인 경우 정렬 기준을 자동으로 세팅)
- 애너테이션 validation들로 넘어온 값들을 검증하며, sort 의 경우 커스텀 애너테이션 `In` 생성/적용해 정렬 기준을 다양하게 받을 수 있도록 처리
- 과제 요구 사항에는 친구 목록과 친구 신청 목록의 응답 형태가 다른데, 친구 목록의 경우 일반적인 페이지네이션, 친구 신청의 경우 커서 페이지네이션 형태로 반환이 필요 -> 페이지네이션을 두 개로 나눠 재활용 가능하도록 처리 후 개별 dto에서 상속 받는 형태로 구현
- 일반 페이지네이션을 사용한다면, 각 요청 객체는 PageRequestDto 를 상속 받아 부모 객체의 toPageable 메서드를 호출해 repository 로직에 넘길 수 있는 Pageable 형태로 변환
- 커서 페이지네이션도 동일한 방식으로, 우선 Cursurable을 이라는 객체를 구성한 다음, 각 요청 객체가 CursorRequestDto 를 상속 받아 부모 객체의 toCursorable 메서드르 호출해 변환 Cursurable 형태로 변환 후 repository로 전달
- 일반 페이지네이션의 경우 과제 명세에는 0페이지를 1페이지 인것 처럼 표기되어있지만, 일반적인 상식으로 0페이지 존재하지 않으므로 최소값을 1로 조정함
    ```java
    public class PageRequestDto {
        private Integer page;
        private Integer maxSize;
  
        public void init() {
            if (page == null) page = 1;
            if (maxSize == null) maxSize = 10;
        }
  
        public Pageable toPageable(String sort) {
            page = Math.max(page - 1, 0);
  
            String[] sortParts = sort.split(",");
            String sortField = sortParts[0];
  
            Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
  
            log.info("order direction: {}", direction);
  
            return PageRequest.of(this.page, this.maxSize, Sort.by(direction, sortField));
        }
    }
    ```
    ```java
    public class CursorPageRequestDto {
        private String requestId;
        private Integer maxSize;
        private String window;
    
        public void init() {
            if (maxSize == null) maxSize = 20;
            if (StringUtils.isBlank(window)) window = "7d";
        }
    
        public Cursorable toCursorable(String sort) {
            String[] sortParts = sort.split(",");
            String order = sortParts[0];
    
            Order direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                  ? Order.ASC
                  : Order.DESC;
    
            String stringParsedRequestId = requestId == null ? null : requestId.toString();
    
            return Cursorable.builder()
                    .order(order)
                    .direction(direction)
                    .requestId(stringParsedRequestId)
                    .maxSize(maxSize)
                    .window(window)
                    .build();
        }
      }
    ```

- 친구 조회의 경우 N + 1 문제와 얽혀있었고, 이를 해결하기 위해 querydsl의 Projections 를 이용해 JPA의 지연로딩을 막고 한번의 쿼리로 데이터 조회 처리 
    ```java
        public class FriendQueryDslRepositoryImpl implements FriendQueryDslRepository {
            ...
            @Override
            public Page<FriendListResponseDto> searchFriends(Long userId, Long fromUserId, Long toUserId, Pageable pageable) {
                ...
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
                ...
            }
        }
    ```

---

### 7.친구 신청 요청 

- 친구 신청자 검증은 컨트롤러단 애너테이션으로, 친구 신청 수신 상대 검증 및 본인에게 스스로 보내는 케이스 검증 등은 서비스 코드 단에서 검증 후 이상 있으면 예외 발생
- 신청자-수신자가 이미 친구이거나, 이미 요청된 친구 신청 건이 존재시 케이스에 신청자/수신자에 적합한 예외 발생
- requestId 는 db 삽입 전에 epoch 정렬 형태로 UUIDv7로 생성하도록 로직 구성한 뒤 requestId, fromUserId, toUserId, requestedAt 데이터를 친구 신청 테이블에 저장
    ```java
        public class FriendRequest {
            private String requestId;
            ...
            @PrePersist
            public void prePersist() {
                if (StringUtils.isBlank(requestId)) {
                    requestId = RandomIdGenerator.epochUuid();
                }
            }
        }
    ```
    ```java
        public final class RandomIdGenerator {
            ...
            public static String epochUuid() {
                return UuidCreator.getTimeOrderedEpoch().toString();
            }
        }
    ```

- 친구 신청 요청 데이터 저장 완료시 친구 신청 내역 내 PENDING STATUS로 데이터 저장
    ```java
        public class FriendRequestService {
            ...
            public void processSubmitFriendRequest() {
                ...
                friendRequestRepository.save(friendRequest);
                insertFriendRequestHistory(friendRequest, FriendRequestStatus.PENDING);
            }
            ...
            private void insertFriendRequestHistory(FriendRequest friendRequest, FriendRequestStatus status) {
                FriendRequestHistory friendRequestHistory = FriendRequestHistory.create(
                    friendRequest,
                    friendRequest.getFromUser(),
                    friendRequest.getToUser(),
                    status
                );
                friendRequestHistoryRepository.save(friendRequestHistory);
            }
        }
    ```

---

### 8.친구 신청 승인 

- 친구 신청 승인자 검증은 컨트롤러단 애너테이션으로, requestId 및 수신자id 부재 검증은 서비스단 코드로 검증 처리 후 이상 존재시 예외 발생
- 친구 신청의 requestId와 신청자id 그리고 수신자id를 이용해 요청을 특정 후 부재시 예외 발생 
- 특정된 친구 신청 및 신청자/수신자 정보로 친구 테이블에 2개 row 삽입
- 친구 테이블은 userId(조회 기준id), fromUserId(신청자Id), toUserId(수신자Id), approvedAt(승인일시) 로 구성되므로 친구 관계는 관계의 양 방향에서 조회가 가능 필요함 -> 조회 기준 id를 신청자/수신자로 각각 설정하고 나머지 데이터는 동일하게 row 2개 삽입  
- 친구 신청 승인 및 친구 데이터 저장 완료시 친구 신청 내역 기록 내 ACCEPTED STATUS로 데이터 저장
- 요청된 친구 신청은 삭제 처리
    ```java
        public class FriendRequestService {
            public void processAcceptFriendRequest(Long toUserId, String requestId, FriendRequestAcceptRequestDto requestDto) {
                if (StringUtils.isBlank(requestId) || requestDto.getFromUserId() == null) {
                    throw new CustomException(ResponseCode.INVALID_VALUES);
                }

                FriendRequest friendRequest = friendRequestRepository.searchFriendRequestByAllIds(
                    requestId,
                    requestDto.getFromUserId(),
                    toUserId
                ).orElseThrow(() -> new CustomException(ResponseCode.FRIEND_REQUEST_NOT_FOUND));

                friendRepository.save(Friend.create(friendRequest.getFromUser(), friendRequest.getFromUser(), friendRequest.getToUser()));
                friendRepository.save(Friend.create(friendRequest.getToUser(), friendRequest.getFromUser(), friendRequest.getToUser()));

                insertFriendRequestHistory(friendRequest, FriendRequestStatus.ACCEPTED);

                friendRequestRepository.delete(friendRequest);
            }
        }
    ```
- 1초/10회 제한은 redis를 이용해 rate limit 을 구현했으나, 테스트 도중 동시에 들어오는 요청의 경우 쉽게 처리할 수 없어 분산 락 적용
  (swagger 내 3. Rate Limit Test API 로 테스트 시 debug창 에러 핸들링 확인 가능)  
    ```java
        public class RedisConfig {
            @Value("${spring.data.redis.host}")
            private String host;
            @Value("${spring.data.redis.port}")
            private int port;
  
            @Bean
            public RedisConnectionFactory redisConnectionFactory() {
                return new LettuceConnectionFactory(host, port);
            }
  
            @Bean
            public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
                return new StringRedisTemplate(factory);
            }
        }
    ```
    ```java
        public class RateLimitService {
            private final StringRedisTemplate redisTemplate;
      
            private static final String RATE_LIMIT_SCRIPT = """
                    local current = redis.call('incr', KEYS[1])
                    if tonumber(current) == 1 then
                        redis.call('pexpire', KEYS[1], ARGV[1])
                    end
                    return current
            """;
      
            public boolean checkRateLimit(String key, int limit, long ttlMillis) {
                DefaultRedisScript<Long> script = new DefaultRedisScript<>(RATE_LIMIT_SCRIPT, Long.class);
                Long count = redisTemplate.execute(script, Collections.singletonList(key), String.valueOf(ttlMillis));
                return count != null && count <= limit;
            }
      
            public boolean tryLock(String key, long ttlMillis) {
                Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(key, "LOCKED", Duration.ofMillis(ttlMillis));
                return Boolean.TRUE.equals(success);
            }
      
            public void releaseLock(String key) {
                redisTemplate.delete(key);
            }
        }
  
    ```

---

### 9.친구 신청 거절 

- 친구 신청 거절자 검증은 컨트롤러단 애너테이션으로, requestId 검증은 서비스단 코드로 검증 처리 후 이상 존재시 예외 발생
- 친구 신청의 requestId와 거절자id를 이용해 요청을 특정 후 부재시 예외 발생 
- 특정된 친구 신청 정보로 친구 신청 내역 데이터 REJECTED STATUS로 추가 후 친구 신청 데이터 삭제
    ```java
        public class FriendRequestService {
            public void processRejectFriendRequest(Long toUserId, String requestId) {
                if (StringUtils.isBlank(requestId)) {
                    throw new CustomException(ResponseCode.INVALID_VALUES);
                }

                FriendRequest friendRequest = friendRequestRepository.searchFriendRequestByRequestAndToUserIds(
                    requestId,
                    toUserId
                ).orElseThrow(() -> new CustomException(ResponseCode.FRIEND_REQUEST_NOT_FOUND));

                insertFriendRequestHistory(friendRequest, FriendRequestStatus.REJECTED);

                friendRequestRepository.delete(friendRequest);
            }
        }
    ```

---
