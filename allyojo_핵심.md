# 나만의 정리

## 이중 provider jwt에 대해

- JwtAuthenticationFilter에서 url보고 user인지 guardian인지 구분
- AuthenticationManager에서 두 개의 provider를 붙임.
  - UserAuthenticationProvider, Guardian~
  - 그 이후 successfulAuthentication에서 role + HMAC512 토큰 발급
  - 이후 모든 요청에 대해 Authorization: Bearer xxx
  - JwtAuthorizationFilter에서 서명검증하고 role 따라 인가

## 위임 권한 모델

- 단순히 보호자 A가 로그인하면 ROLE_GUARDIAN을 가짐
- 근데 이 롤이 있다고 해서 아무 노인에 대한 정보를 수정하면 안되므로 이중인가가 필요
- 즉, 필터에서는 "ROLE_GUARDIAN" 여부만 체크하고, 서비스 레이어에서 실제로 이 보호자가 노인을 관리하는 관계인지를 한번 더 검증함
- 이 검증에 대한걸 guardian table에 user를 집어넣어놓음.

# Allyojo 백엔드 핵심 정리

> 노인 건강관리 앱 'Allyojo'의 백엔드. Java 17 / Spring Boot / Spring Security 6 / JPA(Hibernate) / Gradle / Lombok 기반.
> 노인이 건강 미션(복약·혈압·혈당·식사 등)을 수행하고, 보호자가 원격으로 모니터링하는 플랫폼.
> 7개 도메인 엔티티, 약 40개 REST API를 단독 설계·구현.

---

## 목차

1. [이중 Provider JWT 인증 구조](#1-이중-provider-jwt-인증-구조)
2. [위임 권한 모델 (역할 + 관계 이중 검증)](#2-위임-권한-모델)
3. [알람 반복 요일 — 7비트 정수 압축](#3-알람-반복-요일--7비트-정수-압축)
4. [예외 계층 — BusinessException + ErrorCode](#4-예외-계층)
5. [도메인 규칙 캡슐화 — 혈압 분류 / 수행률 집계](#5-도메인-규칙-캡슐화)
6. [AI 비전 영양소 리포트](#6-ai-비전-영양소-리포트)
7. [CoolSMS 휴대폰 인증](#7-coolsms-휴대폰-인증)
8. [기술 스택 요약](#8-기술-스택-요약)

---

## 1. 이중 Provider JWT 인증 구조

**문제:** 노인(User)과 보호자(Guardian)는 서로 다른 테이블에 저장되는 완전히 다른 사용자 타입이다. 그런데 로그인 처리를 어떻게 분리할 것인가?

**해결:** 요청 경로로 사용자 타입을 분기하고, 두 개의 `AuthenticationProvider`가 자기 담당만 처리하도록 설계했다.

### 전체 흐름

```
[로그인 요청]  POST /login/user  또는  POST /login/guardian
        │
        ▼
JwtAuthenticationFilter  ← URL 보고 "USER_LOGIN" / "GUARDIAN_LOGIN" 꼬리표 부착
        │
        ▼
AuthenticationManager  ← 등록된 두 Provider에게 차례로 물어봄
        ├── UserAuthenticationProvider     → 꼬리표가 USER면 처리, 아니면 null
        └── GuardianAuthenticationProvider → 꼬리표가 GUARDIAN이면 처리, 아니면 null
        │
        ▼
successfulAuthentication  ← 성공하면 role 담아 HMAC512 토큰 발급
        │
[이후 모든 요청]  Authorization: Bearer xxx
        │
        ▼
JwtAuthorizationFilter  ← 서명 검증 후 role 따라 SecurityContext 채움 (무상태)
```

### (1) 요청 경로로 사용자 타입 분기

`JwtAuthenticationFilter` — URL을 보고 토큰에 "꼬리표(details)"를 붙인다.

```java
// JwtAuthenticationFilter.java
String loginUrl = request.getRequestURI();
UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getUsername(),
                loginRequestDTO.getPassword());

if (request.getRequestURI().contains("/login/guardian")) {
    token.setDetails(new WebAuthenticationDetails(request) {
        @Override public String toString() { return "GUARDIAN_LOGIN"; }
    });
} else {
    token.setDetails(new WebAuthenticationDetails(request) {
        @Override public String toString() { return "USER_LOGIN"; }
    });
}
return authenticationManager.authenticate(token);
```

### (2) 두 Provider가 꼬리표를 보고 자기 것만 처리

핵심 트릭: Spring Security는 등록된 Provider를 순서대로 호출하는데, **자기 담당이 아니면 `null`을 반환해 다음 Provider에게 넘긴다.**

```java
// UserAuthenticationProvider.java
@Override
public Authentication authenticate(Authentication authentication) {
    String details = authentication.getDetails() != null
            ? authentication.getDetails().toString() : "";
    if (!"USER_LOGIN".equals(details)) {
        return null;  // 내 담당 아님 → GuardianProvider가 처리하도록 양보
    }
    return super.authenticate(authentication);  // 내 담당 → User 테이블로 인증
}
```

```java
// GuardianAuthenticationProvider.java
@Override
public Authentication authenticate(Authentication authentication) {
    String details = authentication.getDetails() != null
            ? authentication.getDetails().toString() : "";
    if (!"GUARDIAN_LOGIN".equals(details)) {
        return null;  // 다른 provider가 처리하도록 null 반환
    }
    return super.authenticate(authentication);
}
```

두 Provider는 각각 `PrincipalDetailsService`(User 조회), `GuardianDetailsService`(Guardian 조회)를 주입받아 **조회하는 테이블 자체가 다르다.**

```java
// SecurityConfig.java — 두 Provider 등록
builder.authenticationProvider(userAuthenticationProvider)
       .authenticationProvider(guardianAuthenticationProvider);
```

### (3) HMAC512 서명 토큰 발급

```java
// JwtAuthenticationFilter.java — successfulAuthentication()
Object principal = authResult.getPrincipal();
String username = null, role = null;

if (principal instanceof PrincipalDetails) {
    username = ((PrincipalDetails) principal).getUser().getUsername();
    role = "ROLE_USER";
} else if (principal instanceof GuardianDetails) {
    username = ((GuardianDetails) principal).getGuardian().getGuardianName();
    role = "ROLE_GUARDIAN";
}

String jwtToken = JWT.create()
        .withSubject("JWT token")
        .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
        .withClaim("username", username)
        .withClaim("role", role)                       // 역할 정보를 토큰 안에 박음
        .sign(Algorithm.HMAC512(JwtProperties.SECRET)); // 비밀키로 서명
response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
```

> HMAC512는 "비밀키 하나로 서명하고 같은 키로 검증"하는 대칭키 방식. 코드에는 `// 추후 RSA 변경 가능성` 주석으로 비대칭키 확장 여지를 남겨둠.

### (4) 무상태(stateless) 인가 검증

```java
// SecurityConfig.java — 세션을 아예 만들지 않음
.sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

서버가 로그인 상태를 기억하지 않으므로, 매 요청마다 `JwtAuthorizationFilter`가 토큰 서명을 검증하고 role에 따라 인증 정보를 그 요청 동안만 채운다.

```java
// JwtAuthorizationFilter.java
String jwtToken = jwtHeader.substring(JwtProperties.TOKEN_PREFIX.length());
try {
    username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
                  .verify(jwtToken).getClaim("username").asString();   // 서명 검증
    role     = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
                  .verify(jwtToken).getClaim("role").asString();
} catch (Exception e) {
    throw new JwtException(ErrorCode.JWT_SIGNATURE_ERROR);  // 서명 깨지면 일관된 에러
}

if ("ROLE_USER".equals(role)) {
    userRepository.findByUsername(username).ifPresent(u -> {
        PrincipalDetails principalDetails = new PrincipalDetails(u);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    });
} else if ("ROLE_GUARDIAN".equals(role)) {
    guardianRepository.findByGuardianName(username).ifPresent(g -> {
        GuardianDetails guardianDetails = new GuardianDetails(g);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                guardianDetails, null, guardianDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    });
}
```

---

## 2. 위임 권한 모델

단순 RBAC(역할 기반 접근제어)와 차별화되는 지점.

**문제:** 보호자 A가 로그인하면 `ROLE_GUARDIAN`을 갖는다. 하지만 그렇다고 **아무 노인이나** 조회/수정하면 안 된다. "보호자 A가 실제로 노인 B를 담당하는가?"는 역할만으로는 알 수 없다.

**해결:** 필터(인가)는 "보호자인가?"까지만 보고, **서비스 레이어에서 "이 보호자가 이 노인을 관리하는 관계인가?"를 한 번 더 검증**한다. → 권한이 두 겹으로 통제됨.

```java
// GuardianService.java — 노인 데이터를 건드리는 모든 보호자 메서드에 반복되는 패턴
Guardian guardian = guardianRepository.findByGuardianName(guardianName)
        .orElseThrow(GuardianNotFoundException::new);
User user = userRepository.findByUsername(username)
        .orElseThrow(UserNotFoundException::new);

// 보호자가 해당 노인을 관리하는지 확인
if (!guardian.getUsers().contains(user)) {
    throw new UserNotManagedException();   // 관계 없으면 차단
}
```

이 검증이 들어간 메서드: `addAlarmForUser`, `getAlarmsForUser`, `updateAlarmForUser`, `deleteAlarmForUser`, `getReport`, `getBloodPressureReport`, `getFoodReport`.

관계는 보호자-노인을 연결할 때 맺어지고 JPA 연관관계(`Guardian` ↔ `User`)로 저장된다.

```java
// GuardianService.java — addUserToGuardian()
user.addGuardian(guardian);
return userRepository.save(user);
```

---

## 3. 알람 반복 요일 — 7비트 정수 압축

**나쁜 방법(하지 않은 것):** `boolean monday, tuesday, ... sunday;` → DB 컬럼 7개, 쿼리마다 7개 조건.

**실제 구현:** 정수 하나의 7개 비트로 요일을 표현. 비트 연산으로 ON/OFF 제어.

```
비트 인덱스:  6  5  4  3  2  1  0
요일:        일 토 금 목 수 화 월

예) 월/수/금만 ON → 0b0010101 = 21
```

```java
// Alarm.java
@Column(nullable = false) // 알람요일: 7자리 비트
private Integer alarmDays = 0;

// 요일이 켜져 있는지 확인 (AND 연산)
public boolean isAlarmSetForDay(int dayIndex) {   // 0(월) ~ 6(일)
    return (alarmDays & (1 << dayIndex)) != 0;
}

// 요일 켜기 (OR 연산) — 해당 비트만 1로, 나머지는 그대로 유지
public void setAlarmForDay(int dayIndex) {
    alarmDays |= (1 << dayIndex);
}

// 요일 끄기 (AND + NOT 연산) — 해당 비트만 0으로, 나머지는 그대로 유지
public void unsetAlarmForDay(int dayIndex) {
    alarmDays &= ~(1 << dayIndex);
}
```

이 방식 덕에 리포트 집계에서 날짜를 하루씩 돌며 `isAlarmSetForDay()`만 호출하면 그 날 알람이 울려야 했는지 바로 알 수 있다.

```java
// VerificationService.java — 기간 내 알람 발생 횟수 계산
private int calculateTriggeredAlarmCount(Alarm alarm, LocalDateTime startDate, LocalDateTime endDate) {
    int count = 0;
    LocalDateTime current = startDate;
    while (current.isBefore(endDate) || current.isEqual(endDate)) {
        if (alarm.isAlarmSetForDay(current.getDayOfWeek().getValue() - 1)) {
            count++;
        }
        current = current.plusDays(1);
    }
    return count;
}
```

---

## 4. 예외 계층

`BusinessException`을 부모로 두고, `ErrorCode` enum으로 HTTP 상태와 메시지를 중앙 관리. `@RestControllerAdvice` 전역 핸들러가 일관된 응답을 반환.

### 구조

```
RuntimeException
    └── BusinessException  ← 부모 (공통 처리 창구)
            ├── UserNotFoundException        → USER_NOT_FOUND
            ├── GuardianNotFoundException    → GUARDIAN_NOT_FOUND
            ├── AlarmNotFoundException       → ALARM_NOT_FOUND
            ├── MissionNotFoundException     → MISSION_NOT_FOUND
            ├── UserNotManagedException      → USER_NOT_MANAGED
            └── ...
```

```java
// BusinessException.java — 모든 비즈니스 예외의 부모
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;  // HTTP 상태 + 메시지를 한 곳에 묶음
    public BusinessException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

```java
// ErrorCode.java — 모든 에러 목록을 한 파일에서 관리
@AllArgsConstructor
@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error occurred."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 아이디의 유저가 없습니다."),
    GUARDIAN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 아이디의 보호자가 없습니다."),
    USER_NOT_MANAGED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 유저의 보호자로 등록되지 않았습니다."),
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 아이디의 알람이 없습니다."),
    JWT_SIGNATURE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "토큰 서명 중 오류가 발생했습니다."),
    COOLSMS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "휴대전화 인증 과정 중 오류가 발생했습니다.");

    private final Integer status;
    private final String message;
}
```

```java
// 자식 예외는 딱 3줄 — 에러코드만 전달
public class GuardianNotFoundException extends BusinessException {
    public GuardianNotFoundException() {
        super(ErrorCode.GUARDIAN_NOT_FOUND);
    }
}
```

```java
// GlobalExceptionHandler.java — 어떤 자식 예외든 부모 타입 하나로 일괄 처리
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(errorResponse,
                HttpStatusCode.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // @Valid 검증 실패
    protected ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
```

**장점:** 새 도메인 예외 추가 시 `ErrorCode`에 한 줄, 예외 클래스 파일 하나만 만들면 끝. 핸들러는 건드릴 필요 없음.

---

## 5. 도메인 규칙 캡슐화

혈압 단계 자동 분류, 수행률 집계 같은 도메인 규칙을 서비스에 캡슐화해 컨트롤러가 계산 방식을 몰라도 되게 책임을 분리.

### 혈압 단계 자동 분류

```java
// VerificationService.java
private String classifyBloodPressure(Double low, Double high) {
    if (low == null || high == null)                              return "비정상 혈압 데이터 포함";
    if (high < 120 && low < 80)                                   return "정상 혈압";
    if (high >= 140 && low < 90)                                  return "수축기 단독 고혈압";
    if (high >= 120 && high < 130 && low < 80)                    return "주의 혈압";
    if ((high >= 130 && high < 140) || (low >= 80 && low < 90))   return "고혈압 전단계";
    if ((high >= 140 && high < 160) || (low >= 90 && low < 100))  return "고혈압 1기";
    if (high >= 160 || low >= 100)                                return "고혈압 2기";
    return "예외처리";
}
```

### 수행률 집계

```java
// VerificationService.java — 성공 횟수 / 전체 수행 횟수
int denominator = countFalseVerification(username, missionId, startDate, endDate); // 전체(성공+실패)
int numerator   = countTrueVerification(username, missionId, startDate, endDate);  // 성공
double successRatio = (double) numerator / (double) denominator;
```

컨트롤러는 `getBloodPressureReport()` 하나만 호출하면 된다. 분류 기준이나 계산 방식이 바뀌어도 서비스 안에서만 수정하면 되고 컨트롤러·엔티티는 건드릴 필요 없음.

---

## 6. AI 비전 영양소 리포트

프론트의 AI 비전 모델이 **식사 사진에서 음식별 영양소(탄·단·지·나트륨)를 추출해 보내주면**, 백엔드가 저장하고 성별·연령 기반 권장 섭취량 대비 비율 리포트를 생성.

### (1) 추출된 영양소 수신·저장 (사진 한 장에 음식 여러 개 가능)

```java
// NutrientService.java — addFoodVerification()
List<Nutrient> nutrients = dto.getFoods().stream()
        .map(foodDTO -> Nutrient.builder()
                .verificationId(savedVerification.getVerificationId())
                .foodName(foodDTO.getFoodName())
                .verification(savedVerification)
                .carbohydrates(foodDTO.getCarbohydrates())
                .protein(foodDTO.getProtein())
                .fat(foodDTO.getFat())
                .sodium(foodDTO.getSodium())
                .build())
        .toList();
nutrientRepository.saveAll(nutrients);
```

### (2) 성별·연령 기반 권장 섭취량 조회

```java
// NutritionStandardService.java — 성별 × 연령대별 기준표
standards.add(new NutritionStandard(Gender.MALE.name(),   65, 130, 60, 59, 1500, 2300));
//                                    성별            나이상한 탄  단  지  나트륨범위

public NutritionStandard getStandard(String gender, Integer age) {
    return standards.stream()
            .filter(s -> s.getGender().equals(gender) && age <= s.getAgeLimit())
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("기준 데이터를 찾을 수 없습니다."));
}
```

### (3) 하루치 합산 후 권장량 대비 비율 계산

```java
// NutrientService.java — getFoodReport()
for (Nutrient nutrient : nutrients) {   // 그날 먹은 모든 음식 누적 합산
    nutrientTotals.put("carbohydrates", nutrientTotals.get("carbohydrates") + nutrient.getCarbohydrates());
    nutrientTotals.put("protein",       nutrientTotals.get("protein")       + nutrient.getProtein());
    nutrientTotals.put("fat",           nutrientTotals.get("fat")           + nutrient.getFat());
    nutrientTotals.put("sodium",        nutrientTotals.get("sodium")        + nutrient.getSodium());
}

List<Double> percentList = List.of(   // 섭취 / 권장
        nutrientTotals.get("carbohydrates") / standard.getCarbohydrate(),
        nutrientTotals.get("protein")       / standard.getProtein(),
        nutrientTotals.get("fat")           / standard.getFat(),
        nutrientTotals.get("sodium")        / 2000D
);
```

> `Nutrient` 엔티티는 `@EmbeddedId`(verificationId + foodName 복합키)와 `@MapsId`로 Verification과 연결 — "한 검증에 속한 여러 음식"을 표현.

---

## 7. CoolSMS 휴대폰 인증

```java
// CoolSmsService.java
@Value("${coolsms.apiKey}") private String apiKey;   // 민감정보는 코드에 하드코딩 X
@Value("${coolsms.secret}") private String secret;

@PostConstruct  // 의존성 주입 완료 후 SDK 초기화
public void init() {
    this.messageService = NurigoApp.INSTANCE.initialize(apiKey, secret, domain);
}

public String sendVerificationCode(String phoneNumber) {
    String verificationCode = generateVerificationCode();   // 4자리 난수
    Message message = new Message();
    message.setFrom(number);
    message.setTo(phoneNumber);
    message.setText("[Allyojo] 인증번호는 [" + verificationCode + "] 입니다.");
    messageService.sendOne(new SingleMessageSendingRequest(message));
    return verificationCode;
}
```

---

## 8. 기술 스택 요약

| 영역        | 사용 기술 / 설계                                                      |
| ----------- | --------------------------------------------------------------------- |
| 언어/런타임 | Java 17                                                               |
| 프레임워크  | Spring Boot, Spring Security 6                                        |
| 인증        | 이중 `AuthenticationProvider` + JWT(HMAC512), stateless 세션          |
| 인가        | 역할(role) 검증 + 서비스 레이어 관계(위임 권한) 이중 검증             |
| 영속성      | JPA / Hibernate — `@ManyToOne(LAZY)`, `@EmbeddedId`, `@MapsId`        |
| 예외 처리   | `BusinessException` 계층 + `ErrorCode` enum + `@RestControllerAdvice` |
| 검증        | `@Valid` (Bean Validation)                                            |
| 문서화      | SpringDoc OpenAPI (Swagger)                                           |
| 외부 연동   | CoolSMS(휴대폰 인증), 프론트 AI 비전 모델(영양소 추출)                |
| 테스트      | JUnit 5 + `@SpringBootTest` 통합 테스트 (H2)                          |
| 빌드/도구   | Gradle, Lombok                                                        |

### 자기소개 문구 ↔ 코드 대응표

| 문구                | 실제 코드                                                 |
| ------------------- | --------------------------------------------------------- |
| 이중 Provider JWT   | URL 꼬리표 → Provider가 `null`로 양보 → 테이블별 인증     |
| HMAC512 무상태      | `STATELESS` 세션 + 매 요청 서명 검증                      |
| 위임 권한 모델      | `guardian.getUsers().contains(user)` 관계 재검증          |
| 7비트 정수 압축     | `alarmDays`에 AND/OR/NOT 비트 연산                        |
| 예외 중앙 관리      | `BusinessException` + `ErrorCode` + 전역 핸들러           |
| 도메인 캡슐화       | 혈압 분류 / 수행률 집계를 서비스에 격리                   |
| AI 비전 영양 리포트 | 추출 영양소 수신 → 성별·연령 기준 조회 → 권장량 대비 비율 |
| CoolSMS 연동        | 4자리 난수 SMS, 키는 `@Value` 외부 주입                   |
