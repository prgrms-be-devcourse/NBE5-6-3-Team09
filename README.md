## ✨ 주요 기능

### 🍅 **포모도로 학습 관리**

- 사용자 설정 공부 시간 및 휴식 시간
- **실제 집중 시간 vs 계획 시간** 추적
- 카테고리별 학습 진도 관리

### 🤖 **AI 학습 도우미**

- **Gemini AI** 기반 개인화된 학습 계획 추천
- 상황별 맞춤 조언 및 동기부여 메시지
- LangChain4j 통합으로 자연스러운 대화

### 🎤 **면접 연습 시스템**

- 카테고리별 랜덤 질문 출제 (알고리즘, DB, 네트워크 등)
- **키워드 기반 답변 분석** (한국어 조사 처리 포함)
- 실시간 피드백 및 개선점 제시

### 📊 **학습 통계 & 분석**

- 날짜별/카테고리별 집중 시간 분포
- 학습 완료율 및 목표 달성률 추적
- Chart.js 기반 직관적 시각화

### 📅 **스마트 일정 관리**

- 캘린더 기반 Todo 관리
- **실시간 알림** (시작 10분 전 SSE 알림)
- 우선순위 기반 자동 정렬

### 📝 **체계적 회고 시스템**

- 코딩테스트 문제별 상세 회고
- 면접준비 학습 내용 정리
- 개선점 및 향후 계획 수립

## 🛠 기술 스택

### Backend

- **Framework**: Spring Boot 3.4.5, Spring Security 6
- **Database**: MySQL 8.0, Redis 6.0
- **ORM**: JPA + Hibernate, QueryDSL 5.0
- **AI**: LangChain4j + Google Gemini 2.0

### Frontend

- **Template**: Thymeleaf
- **JavaScript**: Chart.js, FullCalendar, jQuery
- **Real-time**: Server-Sent Events (SSE)

### Infrastructure

- **Build**: Maven
- **Security**: BCrypt, CSRF Protection
- **Monitoring**: Spring Boot Actuator


## 📁 프로젝트 구조

```
src/main/java/com/grepp/codemap/
├── 🔧 infra/           # 인프라 설정 (Security, Config, Utils)
├── 👥 admin/           # 관리자 기능 (대시보드, 사용자 관리)
├── 👤 user/            # 사용자 관리 (인증, 프로필)
├── 📋 routine/         # 루틴 관리 (포모도로, 회고)
├── 📅 todo/            # Todo 관리 (캘린더, 알림)
├── 🎤 interview/       # 면접 연습 (질문, 답변 분석)
├── 🤖 chatbot/         # AI 챗봇 (학습 추천)
├── 🔔 alert/           # 실시간 알림 (SSE)
├── 🎯 jobposting/      # 채용 공고 관리
└── 📊 mypage/          # 마이페이지 (통계, 설정)

```

## 🎯 핵심 구현 특징

### 🕐 정확한 시간 추적

```java
// 계획 시간 vs 실제 집중 시간 분리 추적
public Integer getActualDurationMinutes() {
    long seconds = Duration.between(startedAt, endTime).getSeconds();
    return seconds < 60 ? 0 : (int) Math.ceil(seconds / 60.0);
}

```

### 🔍 한국어 키워드 분석

```java
// 조사 제거 후 정확한 키워드 매칭
private boolean isKeywordMatched(String text, String keyword) {
    // "스택이" → "스택"으로 정규화 후 매칭
    String textWithoutParticles = removeParticlesFromText(text);
    return exactPattern.matcher(textWithoutParticles).find();
}

```

### 🤖 AI 통합

```java
@AiService
public interface Assistant {
    @SystemMessage("너는 개발자 취업준비생들의 일정 플래너 챗봇이야.")
    String recommendPlan(@UserMessage String keyword);
}

```

### ⚡ 실시간 알림

```java
@Scheduled(fixedRate = 30000) // 30초마다 체크
public void checkTodosStartingSoon() {
    // 시작 10분 전 SSE 알림 발송
}

```

## 📊 데이터베이스 설계

### 핵심 테이블

- **users**: 사용자 정보 및 설정
- **daily_routines**: 학습 루틴 (포모도로 세션과 연결)
- **pomodoro_sessions**: 실제 집중 시간 기록
- **interview_questions**: 면접 질문 은행
- **todos**: 일정 관리
- **coding_test_reviews**: 코딩테스트 회고
- **interview_reviews**: 면접준비 회고

### 관계 특징

- **사용자 중심 설계**: 모든 데이터가 users 테이블과 연관
- **Soft Delete**: 데이터 보존을 위한 논리 삭제
- **실시간 추적**: 시작/종료 시간 정확한 기록

## 🔐 보안 & 인증

- **Spring Security 6**: 역할 기반 접근 제어
- **BCrypt**: 비밀번호 암호화
- **CSRF Protection**: 요청 위조 방지
- **세션 기반 인증**: 안정적인 상태 관리

## 📈 성능 최적화

- **QueryDSL**: 타입 안전한 동적 쿼리
- **Redis**: 세션 저장 및 이벤트 처리
- **HikariCP**: 효율적인 커넥션 풀 관리
- **지연 로딩**: N+1 문제 방지

## 🎨 사용자 경험

- **반응형 디자인**: 모바일/태블릿 지원
- **직관적 UI**: 커스텀 CSS 기반 일관된 디자인
- **실시간 피드백**: 즉각적인 상태 업데이트
- **Progressive Enhancement**: 기본 기능 우선, 고급 기능 점진적 추가


## 🤝 기여하기

1. 이슈 등록 또는 기능 제안
2. Fork 후 feature 브랜치 생성
3. 코드 작성 및 테스트
4. Pull Request 생성


---
