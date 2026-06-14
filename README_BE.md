# AllyoJo - 노인 건강관리 앱 백엔드

> **담당 범위:** 백엔드 전체 단독 설계 및 구현  
> **프로젝트 유형:** 캡스톤 디자인 (서울시립대학교 컴퓨터과학부)

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [기술 스택](#2-기술-스택)
3. [시스템 아키텍처](#3-시스템-아키텍처)
4. [도메인 설계 및 ERD](#4-도메인-설계-및-erd)
5. [인증 / 인가 설계](#5-인증--인가-설계)
6. [핵심 구현 내용](#6-핵심-구현-내용)
7. [API 목록](#7-api-목록)
8. [예외 처리 전략](#8-예외-처리-전략)
9. [구현 범위 요약](#9-구현-범위-요약)

---

## 1. 프로젝트 개요

AllyoJo는 고령자의 건강 미션 수행을 돕고, 보호자가 원격으로 모니터링할 수 있는 노인 건강관리 플랫폼이다.

**노인(User)** 은 앱을 통해 복약, 혈당 측정, 혈압 측정, 식사 기록 등의 미션을 알람 기반으로 수행하고, **보호자(Guardian)** 는 노인의 미션 수행 현황과 건강 지표 리포트를 확인하며 알람을 원격으로 관리한다.

AI 비전 모델(프론트엔드 측)이 식사 사진을 분석해 음식별 영양소 정보를 추출하면, 백엔드는 이를 수신·저장하고 사용자의 성별·연령 기반 권장 영양소 기준과 비교한 리포트를 생성한다.

---

## 2. 기술 스택

| 구분 | 사용 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.4 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security 6, JWT (HMAC512) |
| Database | MariaDB (+ MySQL Connector) |
| External API | CoolSMS (휴대폰 인증) |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Gradle |
| Validation | Jakarta Bean Validation (`@Valid`) |

---

## 3. 시스템 아키텍처

```
┌─────────────────────────────────────────────────────┐
│                  Frontend (Mobile App)               │
│                                                      │
│  ┌─────────────────┐      ┌─────────────────────┐   │
│  │   UI / Logic    │      │  AI Vision Model    │   │
│  │                 │      │  (식사 사진 인식)    │   │
│  │                 │◄────►│  음식명 + 영양소 추출│   │
│  └────────┬────────┘      └──────────┬──────────┘   │
│           │ REST API                 │ 파싱된 데이터  │
└───────────┼──────────────────────────┼──────────────┘
            │                          │
            ▼                          ▼
┌─────────────────────────────────────────────────────┐
│               Spring Boot Backend                    │
│                                                      │
│  JWT Filter ──► Controller ──► Service ──► JPA      │
│                                            │         │
│                                            ▼         │
│                                         MariaDB      │
└─────────────────────────────────────────────────────┘
            │
            ▼
     CoolSMS (휴대폰 인증 외부 API)
```

### 데이터 흐름 요약

1. **인증:** 노인/보호자 각각 별도 경로로 로그인 → JWT 발급 → 이후 모든 요청 헤더에 Bearer 토큰 포함
2. **알람 수행:** 노인이 알람을 수행하면 FE가 `POST /verification/add` 또는 `POST /food/add` 호출 → 수행 기록 저장
3. **식사 기록:** FE의 AI 모델이 식사 사진을 분석 → 음식명·영양소(탄수화물/단백질/지방/나트륨) 파싱 → BE로 전송 → BE가 저장 후 권장 섭취량 대비 비율 계산
4. **리포트:** 보호자/노인이 날짜 범위와 미션 유형을 지정해 리포트 조회 → 수행률, 평균 수치, 건강 단계 분류 반환

---

## 4. 도메인 설계 및 ERD

### 엔티티 관계

```
Guardian (보호자)
  └──< User (노인)            Many-to-One: 여러 노인을 한 보호자가 관리
        ├──< Alarm            Many-to-One: 노인은 여러 알람 보유
        │      │
        │      └── Mission    Many-to-One: 알람은 하나의 미션 유형에 속함
        │
        ├──< Verification     알람 수행 결과 기록
        │      └──< Nutrient  식사 미션 전용: 음식별 영양소 (복합 PK)
        │
        └──< Todo             독립적인 할 일 목록
```

### 엔티티별 설계 포인트

**User**
- `username`: 로그인 ID (유니크)
- `phoneNumber`: 유니크, 보호자-노인 연결 시 전화번호 기반 검색에 활용
- `userGrade`: BASIC / PREMIUM (등급 기반 기능 분리 구조 준비)
- `guardian`: `@ManyToOne(fetch = LAZY)` - 보호자 연결은 회원가입 시 또는 사후에 가능

**Alarm**
- `alarmDays`: **7비트 정수**로 요일 정보 압축 저장 (월=bit0 ~ 일=bit6)
  - 비트 연산(`isAlarmSetForDay`, `setAlarmForDay`, `unsetAlarmForDay`)으로 요일별 ON/OFF 제어
- `createdByGuardian`: 보호자가 생성한 알람과 노인이 직접 생성한 알람을 구분하는 플래그
- `disabled`: 알람 비활성화 플래그. delete API는 물리 삭제, 이 필드는 update API를 통해 노출 여부를 별도 제어하는 용도
- `restrictAlarm`: 엄격 모드 - 알람 강제 수행 여부
- `alarmInterval`: 반복 간격 (분 단위 정수)

**Verification**
- `value`: 혈당 수치 또는 복약 수행 여부
- `value2`: 혈압 미션에서 이완기 혈압 저장 (수축기는 `value`)
- `result`: true(성공) / false(미수행) - 리포트 수행률 계산 기준

**Nutrient**
- 복합 PK (`NutrientId`): `verificationId` + `foodName`
  - 같은 식사 기록 내에서 음식명이 PK 역할을 하는 구조
- AI 비전 모델이 추출한 음식별 영양소(탄수화물/단백질/지방/나트륨)를 그대로 수신·저장

**Mission** (사전 정의 마스터 데이터)

| missionId | missionName |
|---|---|
| 1 | Eat Medician (복약) |
| 2 | Eat food (식사) |
| 3 | Manage blood pressure (혈압) |
| 4 | Manage blood sugar (혈당) |

---

## 5. 인증 / 인가 설계

### 이중 사용자 타입 JWT 인증 구조

일반적인 Spring Security 구성과 달리, **노인**과 **보호자** 두 가지 사용자 유형이 동일한 `/login/**` URL 패턴을 사용하면서 서로 다른 인증 로직을 거치도록 설계했다.

```
POST /login/user      → UserAuthenticationProvider
POST /login/guardian  → GuardianAuthenticationProvider
```

#### 인증 흐름

```
[1] 클라이언트 → POST /login/user or /login/guardian
        ↓
[2] JwtAuthenticationFilter.attemptAuthentication()
    - Request URI로 USER / GUARDIAN 분기
    - UsernamePasswordAuthenticationToken 생성
    - token.details에 "USER_LOGIN" or "GUARDIAN_LOGIN" 세팅
        ↓
[3] AuthenticationManager → 등록된 Provider 순서로 시도
    - UserAuthenticationProvider: username으로 User 조회, BCrypt 비밀번호 검증
    - GuardianAuthenticationProvider: guardianName으로 Guardian 조회, 검증
        ↓
[4] 인증 성공 → JwtAuthenticationFilter.successfulAuthentication()
    - Principal 타입(PrincipalDetails or GuardianDetails)으로 role 결정
    - JWT 생성: HMAC512, claim에 username + role(ROLE_USER or ROLE_GUARDIAN) 포함
    - 응답 헤더 Authorization: Bearer <token> 반환
```

#### 인가 흐름

```
[1] 이후 모든 API 요청
        ↓
[2] JwtAuthorizationFilter.doFilterInternal()
    - Authorization 헤더 추출 및 Bearer 접두어 검증
    - HMAC512로 서명 검증, username + role 클레임 파싱
        ↓
[3] role에 따라 분기
    - ROLE_USER   → UserRepository에서 User 조회 → PrincipalDetails → SecurityContext 세팅
    - ROLE_GUARDIAN → GuardianRepository에서 Guardian 조회 → GuardianDetails → SecurityContext 세팅
        ↓
[4] Controller에서 @PathVariable guardianName 등으로 추가 권한 검증 수행
```

#### 보호자 권한 검증 (서비스 레이어)

단순 역할 검증을 넘어, **보호자가 실제로 해당 노인을 관리하는 관계인지** 서비스 레이어에서 명시적으로 검증한다.

```java
// GuardianService 전체에 공통 적용
if (!guardian.getUsers().contains(user)) {
    throw new UserNotManagedException(); // 403 성격의 커스텀 예외
}
```

---

## 6. 핵심 구현 내용

### 6-1. 알람 요일 비트마스크 설계

알람 반복 요일을 7개 boolean 컬럼으로 저장하는 대신, **7비트 정수 하나**로 압축한다.

```
bit index:  6  5  4  3  2  1  0
요일:       일 토 금 목 수 화 월
예시(월수금): 0  0  1  0  1  0  1 = 21
```

```java
// 특정 요일 알람 여부 조회
public boolean isAlarmSetForDay(int dayIndex) {
    return (alarmDays & (1 << dayIndex)) != 0;
}

// 특정 요일 알람 설정
public void setAlarmForDay(int dayIndex) {
    alarmDays |= (1 << dayIndex);
}

// 특정 요일 알람 해제
public void unsetAlarmForDay(int dayIndex) {
    alarmDays &= ~(1 << dayIndex);
}
```

이 방식으로 컬럼 수 절감, 비트 연산으로 O(1) 조회, 클라이언트와 정수 하나로 직렬화/역직렬화가 가능하다.

---

### 6-2. 건강 리포트 생성 로직

**수행률 계산**

단순 "수행 횟수 / 알람 횟수" 계산이 아닌, DB에 기록된 `result = true / false` 데이터를 기반으로 집계한다.

```java
// 분모: 해당 기간 내 미션에 해당하는 전체 기록 수 (성공 + 실패)
int denominator = verificationRepository.countFalseByUserAndAlarmMissionMissionId(...);
// 분자: result = true인 기록 수
int numerator   = verificationRepository.countTrueByUserAndAlarmMissionMissionId(...);
double successRatio = (double) numerator / denominator;
```

**혈압 단계 자동 분류**

평균 수축기/이완기 혈압값을 받아 대한고혈압학회 기준에 따라 텍스트 분류 반환한다.

```java
private String classifyBloodPressure(Double low, Double high) {
    if (high < 120 && low < 80)                         return "정상 혈압";
    if (high >= 120 && high < 130 && low < 80)          return "주의 혈압";
    if ((high >= 130 && high < 140) || (low >= 80 && low < 90)) return "고혈압 전단계";
    if ((high >= 140 && high < 160) || (low >= 90 && low < 100)) return "고혈압 1기";
    if (high >= 160 || low >= 100)                      return "고혈압 2기";
    if (high >= 140 && low < 90)                        return "수축기 단독 고혈압";
    return "예외처리";
}
```

---

### 6-3. AI 연동 식사 기록 및 영양소 분석

FE의 AI 비전 모델이 식사 사진을 분석해 음식별 영양소를 추출하면, BE는 이를 수신·저장하고 성별/연령 기반 권장 섭취량 대비 비율을 계산해 반환한다.

**수신 DTO 구조**
```json
{
  "alarmId": 1,
  "username": "user1",
  "verificationDateTime": "2024-11-01T12:00:00",
  "result": true,
  "foods": [
    { "foodName": "흰쌀밥", "carbohydrates": 65.1, "protein": 4.2, "fat": 0.5, "sodium": 4.0 },
    { "foodName": "된장찌개", "carbohydrates": 8.3, "protein": 5.1, "fat": 2.3, "sodium": 980.0 }
  ]
}
```

**영양소 기준 데이터 (`NutritionStandardService`)**

외부 DB나 AI 없이 성별·연령 구간별 권장 영양소 기준을 인메모리 테이블로 관리한다.

| 성별 | 나이 기준 | 탄수화물(g) | 단백질(g) | 지방(g) | 나트륨(mg) |
|---|---|---|---|---|---|
| 남성 | ~50세 | 130 | 65 | 67 | 1500~2300 |
| 남성 | ~65세 | 130 | 60 | 59 | 1500~2300 |
| 남성 | ~75세 | 130 | 60 | 54 | 1300~2100 |
| 남성 | 75세 이상 | 130 | 60 | 51 | 1100~1700 |
| 여성 | (동일 구간, 별도 기준) | ... | ... | ... | ... |

리포트 응답 예시:
```json
{
  "foodAverages": [
    {
      "carbohydrates_percent": 0.83,
      "protein_percent": 1.12,
      "fat_percent": 0.74,
      "sodium_percent": 0.61
    }
  ]
}
```

---

### 6-4. 보호자의 노인 알람 원격 관리

보호자는 자신이 관리하는 노인의 알람을 **대신 생성·수정·삭제**할 수 있다. 이때 두 가지 권한 체크가 이중으로 작동한다.

1. **JWT 레이어:** `ROLE_GUARDIAN` 역할인지 검증
2. **서비스 레이어:** 해당 보호자가 해당 노인을 실제로 관리하는 관계인지 검증

보호자가 생성한 알람은 `createdByGuardian = true` 플래그로 마킹되어, 조회 시 노인이 직접 생성한 알람과 구분된다.

```java
// AlarmRepository - 보호자가 생성한 알람만 조회
@Query("select a from Alarm a where a.user.guardian.guardianName = :guardianName " +
       "and a.createdByGuardian = true and a.disabled = false")
List<Alarm> findAllByGuardianName(@Param("guardianName") String guardianName);
```

---

### 6-5. 보호자-노인 연결 방식 이중 지원

보호자와 노인의 관계는 두 가지 시점에서 설정 가능하도록 설계했다.

| 방식 | 엔드포인트 | 설명 |
|---|---|---|
| 가입 시 즉시 연결 | `POST /guardian/join` | 요청 바디에 `seniorName` 포함 시 자동 연결 |
| 가입 후 연결 | `PUT /guardian/addUser` | 노인의 **전화번호**로 검색해 사후 연결 |

전화번호 기반 연결은 노인이 앱을 먼저 가입하고, 보호자가 나중에 연결하는 실사용 시나리오를 반영한 설계다.

---

### 6-6. CoolSMS 휴대폰 인증 연동

회원가입 전 휴대폰 인증을 통해 실제 사용자임을 검증한다.

- `@PostConstruct`로 서버 시작 시 CoolSMS SDK 초기화
- 4자리 랜덤 인증번호 생성 후 SMS 발송
- API Key, Secret, 발신번호를 `application.yml` 환경 변수로 분리 관리

---

### 6-7. JPQL 기반 복잡 쿼리

단순 메서드명 쿼리로 표현하기 어려운 조건들은 `@Query`로 직접 JPQL을 작성했다.

```java
// 특정 유저, 특정 미션, 날짜 범위, result = true인 Verification 수 집계
@Query("SELECT COUNT(v) FROM Verification v " +
       "WHERE v.user.username = :username " +
       "AND v.alarm.mission.missionId = :missionId " +
       "AND v.verificationDateTime BETWEEN :startDate AND :endDate " +
       "AND v.result = true")
int countTrueByUserAndAlarmMissionMissionId(...);

// 유저가 한 번이라도 수행한 미션 이름 중복 제거 조회
@Query("select distinct v.alarm.mission.missionName from Verification v " +
       "where v.user.username = :username")
List<String> findDistinctMissionNamesByUsername(String username);

// 보호자 산하 모든 노인의 알람 조회 (3-depth JOIN)
@Query("select a from Alarm a where a.user.guardian.guardianName = :guardianName " +
       "and a.createdByGuardian = true and a.disabled = false")
List<Alarm> findAllByGuardianName(@Param("guardianName") String guardianName);
```

---

## 7. API 목록

### 인증

| Method | URL | 설명 |
|---|---|---|
| POST | `/login/user` | 노인 로그인, JWT 발급 |
| POST | `/login/guardian` | 보호자 로그인, JWT 발급 |

### 유저 (노인)

| Method | URL | 설명 |
|---|---|---|
| POST | `/user/join` | 노인 회원가입 |
| GET | `/user/{username}` | 유저 정보 조회 |
| GET | `/user/{username}/check` | 아이디 중복 확인 |
| GET | `/user/{phoneNumber}/checkphone` | 전화번호 중복 확인 |

### 보호자

| Method | URL | 설명 |
|---|---|---|
| POST | `/guardian/join` | 보호자 회원가입 |
| GET | `/guardian/{guardianName}` | 보호자 정보 조회 |
| GET | `/guardian/{guardianName}/check` | 보호자 아이디 중복 확인 |
| GET | `/guardian/{phoneNumber}/checkphone` | 보호자 전화번호 중복 확인 |
| PUT | `/guardian/addUser` | 보호자-노인 사후 연결 |
| GET | `/guardian/{guardianName}/users` | 관리 중인 노인 목록 조회 |
| POST | `/guardian/{g}/user/{u}/add` | 노인 알람 대리 생성 |
| GET | `/guardian/{g}/alarms` | 보호자가 만든 모든 알람 조회 |
| GET | `/guardian/{g}/user/{u}/alarms` | 특정 노인 알람 조회 |
| PUT | `/guardian/{g}/user/{u}/alarms/update` | 노인 알람 수정 |
| DELETE | `/guardian/{g}/user/{u}/alarms/delete/{alarmId}` | 노인 알람 삭제 |
| POST | `/guardian/report` | 노인 건강 리포트 조회 (혈당/복약) |
| POST | `/guardian/report/bloodPressure` | 노인 혈압 리포트 조회 |
| POST | `/guardian/report/food` | 노인 식사 리포트 조회 |

### 알람

| Method | URL | 설명 |
|---|---|---|
| POST | `/alarm/add` | 알람 생성 |
| GET | `/alarm/{username}` | 유저 알람 목록 조회 |
| PUT | `/alarm/update` | 알람 수정 |
| DELETE | `/alarm/delete/{alarmId}` | 알람 삭제 |

### 미션 수행 기록 (Verification)

| Method | URL | 설명 |
|---|---|---|
| POST | `/verification/add` | 미션 수행 결과 저장 (혈당/혈압/복약) |
| GET | `/verification/reports/{username}` | 수행한 적 있는 미션 이름 목록 조회 |
| POST | `/verification/report` | 기간별 리포트 조회 (혈당/복약) |
| POST | `/verification/report/bloodPressure` | 기간별 혈압 리포트 조회 |

### 식사 기록

| Method | URL | 설명 |
|---|---|---|
| POST | `/food/add` | AI 분석 결과(음식별 영양소) 저장 |
| POST | `/food/report` | 일별 식사 리포트 조회 |

### 휴대폰 인증

| Method | URL | 설명 |
|---|---|---|
| POST | `/coolsms` | 인증번호 생성 및 SMS 발송 (검증은 FE에서 처리) |

### 할일

| Method | URL | 설명 |
|---|---|---|
| POST | `/todo/add` | 할일 추가 |
| GET | `/todo/{username}` | 할일 + 조건부 알람 목록 조회 |
| GET | `/todo/{username}/only_todo` | 할일만 조회 (알람 제외) |
| PUT | `/todo/update` | 할일 수정 |
| DELETE | `/todo/delete/{todoId}` | 할일 삭제 |

### 미션 (관리자용)

| Method | URL | 설명 |
|---|---|---|
| POST | `/mission/add` | 미션 추가 |
| GET | `/mission/findAll` | 전체 미션 조회 |
| PUT | `/mission/update` | 미션 수정 |
| DELETE | `/mission/delete/{missionId}` | 미션 삭제 |

**총 API 엔드포인트: 약 40개**

---

## 8. 예외 처리 전략

`@RestControllerAdvice`를 활용한 글로벌 예외 처리 구조를 채택했다.

```
Exception 계층:
  BusinessException (공통 부모)
    ├── UserNotFoundException
    ├── GuardianNotFoundException
    ├── UserNotManagedException      ← 보호자-노인 관계 위반
    ├── AlarmNotFoundException
    ├── MissionNotFoundException
    ├── TodoNotFoundException
    ├── UserPhoneNumberDuplicatedException
    └── GuardianPhoneNumberDuplicatedException
  JwtException                       ← JWT 서명 오류 전용
```

모든 예외는 `ErrorCode` enum으로 HTTP 상태 코드와 메시지를 중앙 관리하며, 클라이언트에게 일관된 `ErrorResponse` 형식으로 반환된다.

`@Valid` 유효성 검증 실패는 `MethodArgumentNotValidException`을 핸들링해 필드별 에러 메시지를 Map 형태로 반환한다.

---

## 9. 구현 범위 요약

| 항목 | 내용 |
|---|---|
| 담당 역할 | 백엔드 전체 단독 설계·구현 |
| 도메인 엔티티 | 7개 (User, Guardian, Alarm, Mission, Verification, Nutrient, Todo) |
| API 엔드포인트 | 약 40개 |
| 사용자 역할 | 2종 (ROLE_USER, ROLE_GUARDIAN) |
| 외부 연동 | CoolSMS (SMS 인증) |
| 인증 방식 | Stateless JWT (HMAC512), 이중 Provider 구조 |
| 주요 설계 포인트 | 비트마스크 요일 인코딩, 이중 사용자 타입 JWT, 보호자 위임 권한 모델, AI 분석 결과 수신 및 영양소 리포트 생성 |
