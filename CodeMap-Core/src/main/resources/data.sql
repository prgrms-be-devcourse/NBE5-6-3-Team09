-- 외래키 제약조건 체크 비활성화 (데이터 삽입 중 참조 오류 방지)
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 사용자 데이터 생성 (먼저 생성해야 함 - 다른 테이블에서 참조함)
-- 테스트용 비밀번호 정보:
-- frontend@example.com : 1234
-- backend@example.com : 1234
-- fullstack@example.com : 1234
-- admin@example.com : 1234
INSERT INTO users (email, password, nickname, role, is_active, notification_enabled, alert_stretch_enabled, created_at, updated_at)
VALUES
    ('frontend@example.com', '{noop}1234', 'Front', 'ROLE_USER', true, true, false, NOW() - INTERVAL 7 DAY, NOW()),
    ('backend@example.com', '{noop}1234', 'Back', 'ROLE_USER', true, true, true, NOW() - INTERVAL 5 DAY, NOW()),
    ('fullstack@example.com', '{noop}1234', 'Full', 'ROLE_USER', true, false, false, NOW() - INTERVAL 10 DAY, NOW()),
    ('admin@example.com', '{noop}1234', 'Admin', 'ROLE_ADMIN', true, true, true, NOW() - INTERVAL 30 DAY, NOW());

-- 2. 면접 질문 데이터 생성 (다른 테이블에서 참조할 수 있으므로 먼저 생성)
INSERT INTO interview_questions (question_text, answer_text, category, difficulty, keywords)
VALUES ('퀵 정렬(Quick Sort)의 시간복잡도와 작동 원리를 설명해주세요.', '퀵 정렬은 기준값을 중심으로 분할과 재귀를 통해 정렬하며 평균 시간복잡도는 O(n log n)입니다.', '알고리즘',
        '상', '기준값, 분할, 재귀, n log n'),
       ('DFS와 BFS의 차이점을 설명해주세요.', 'DFS는 깊이우선 탐색으로 재귀나 스택을 사용하고, BFS는 너비우선 탐색으로 큐를 사용합니다.', '알고리즘', '중',
        '깊이우선, 너비우선, 스택, 큐, 재귀'),
       ('이진 탐색(Binary Search)의 전제 조건과 시간복잡도는?', '이진 탐색은 정렬된 배열에서 분할 정복을 통해 O(log n)의 시간복잡도로 탐색합니다.', '알고리즘', '하',
        '정렬된 배열, log n, 분할 정복'),
       ('다익스트라 알고리즘은 어떤 문제에 사용되나요?', '다익스트라 알고리즘은 가중치가 있는 그래프에서 최단경로를 찾기 위해 우선순위 큐를 활용합니다.', '알고리즘', '상',
        '최단경로, 가중치, 그래프, 우선순위 큐'),
       ('조합과 순열의 차이점은?', '순열은 순서를 고려한 경우이고 조합은 순서를 고려하지 않으며 각각 nPr, nCr로 계산합니다.', '알고리즘', '하', '순서, 조합, 순열, nCr, nPr'),
       ('스택과 큐의 차이점은 무엇인가요?', '스택은 LIFO 구조로 후입선출 방식이고, 큐는 FIFO 구조로 선입선출 방식입니다.', '자료구조', '하', 'LIFO, FIFO, 후입선출, 선입선출'),
       ('해시 충돌은 어떻게 해결하나요?', '해시 충돌은 체이닝, 개방주소법, 선형조사, 이차조사 등의 방법으로 해결할 수 있습니다.', '자료구조', '중',
        '체이닝, 개방주소법, 선형조사, 이차조사'),
       ('이진 트리와 이진 탐색 트리의 차이는?',
        '이진 트리는 노드당 최대 두 개의 자식을 갖는 일반적인 트리 구조이고, 이진 탐색 트리는 왼쪽에 작은 값, 오른쪽에 큰 값을 두는 정렬된 트리로 탐색이 효율적입니다.', '자료구조', '중',
        '왼쪽,작은 값, 오른쪽, 큰 값, 정렬, 탐색'),
       ('인접 리스트와 인접 행렬의 차이는?', '인접 리스트는 간선이 적은 경우 메모리 절약에 유리하고, 인접 행렬은 연결 여부를 바로 확인할 수 있어 빠른 접근이 가능합니다.', '자료구조', '상',
        '메모리 절약, 빠른 접근'),
       ('우선순위 큐는 어떤 자료구조로 구현하나요?', '우선순위 큐는 힙을 사용하여 최대힙이나 최소힙으로 완전이진트리 형태로 구현합니다.', '자료구조', '상', '힙, 최대힙, 최소힙, 완전이진트리'),
       ('정규화의 목적은 무엇인가요?', '정규화는 중복 제거와 무결성 유지를 위해 수행되며, 1NF, 2NF, 3NF와 같은 단계로 나뉩니다.', '데이터베이스', '중',
        '중복 제거, 무결성, 1NF, 2NF, 3NF'),
       ('트랜잭션과 ACID란?', '트랜잭션은 작업 단위 이며, ACID는 원자성, 일관성, 고립성, 지속성을 의미합니다.', '데이터베이스', '중', '원자성, 일관성, 고립성, 지속성, 작업 단위'),
       ('인덱스의 장단점은?', '인덱스는 B-Tree 등을 사용해 검색을 빠르게 하지만 저장공간과 쓰기 성능에 영향을 줍니다.', '데이터베이스', '상',
        '검색 빠름, 저장공간, 쓰기 성능, B-Tree'),
       ('JOIN의 종류에는 어떤 것이 있나요?', 'JOIN에는 INNER, LEFT, RIGHT, FULL 등이 있으며 다양한 방식으로 데이터를 조인할 수 있습니다.', '데이터베이스', '하',
        'INNER, LEFT, RIGHT, FULL'),
       ('SQL Injection이란?',
        'SQL Injection은 쿼리 조작을 통해 시스템을 공격하는 기법이며, PreparedStatement를 사용하는 등 보안을 강화하는 방법으로 방지할 수 있습니다.', '데이터베이스', '상',
        '쿼리 조작, 공격, PreparedStatement, 보안'),
       ('프로세스와 스레드의 차이는?',
        '프로세스는 독립 실행 단위 이며, 각각 별도의 메모리 공간과 컨텍스트를 가집니다. 반면, 스레드는 프로세스 내에서 메모리 공유가 가능하며, 보다 가벼운 작업 단위로 동작합니다.', '운영체제',
        '중', '독립 실행, 작업 단위, 메모리 공유, 컨텍스트'),
       ('교착 상태란?',
        '교착 상태 또는 데드락은 둘 이상의 프로세스가 자원 점유 상태에서 서로가 필요한 자원을 놓지 않고 대기상태 에 빠져, 더 이상 진행되지 못하는 상황을 말합니다. 이를 해결하기 위해 예방 또는 회피 기법이 필요합니다.',
        '운영체제', '상', '자원 점유, 대기상태, 예방, 회피, 데드락'),
       ('컨텍스트 스위칭이란?', '컨텍스트 스위칭은 CPU 전환 시 프로세스의 상태를 PCB 상태를 저장 하고, 다시 실행할 때 이를 복원 하는 과정이며 오버헤드가 발생합니다.', '운영체제', '중',
        'CPU 전환, 저장, 복원, PCB, 오버헤드'),
       ('CPU 스케줄링 알고리즘에는 어떤 것이 있나요?', 'CPU 스케줄링에는 FCFS, SJF, RR, Priority 등의 알고리즘이 있으며 선점형과 비선점형 방식이 있습니다.', '운영체제',
        '상', 'FCFS, SJF, RR, Priority, 선점형, 비선점형'),
       ('세마포어는 무엇인가요?', '세마포어는 공유 자원에 대한 접근을 동기화 하는 방법으로 P연산과 V연산을 통해 접근 제어를 합니다.', '운영체제', '하',
        '동기화, 공유 자원, 접근 제어, P연산, V연산'),
       ('HTTP와 HTTPS의 차이는?', 'HTTPS는 SSL을 이용하여 데이터를 암호화 하고 인증서를 통해 보안을 강화하며 포트 443을 사용합니다.', '네트워크', '하',
        'SSL, 암호화, 보안, 포트 443, 인증서'),
       ('TCP와 UDP의 차이는?', 'TCP는 연결지향, 신뢰성 있는 전송 프로토콜이고, UDP는 비연결지향, 비신뢰성 전송 프로토콜입니다.', '네트워크', '중',
        '신뢰성, 비신뢰성, 연결지향, 비연결지향'),
       ('3-way handshake란?', '3-way handshake는 TCP 연결 설정 과정으로 SYN, SYN-ACK, ACK 3단계를 거칩니다.', '네트워크', '중',
        'SYN, SYN-ACK, ACK, 연결 설정, TCP'),
       ('DNS란?', 'DNS는 도메인 이름을 IP 주소로 변환 해주는 시스템으로, 계층 구조로 구성된 네임서버를 이용합니다.', '네트워크', '하',
        '도메인, IP 주소, 변환, 계층 구조, 네임서버'),
       ('OSI 7계층은 무엇인가요?', 'OSI 7계층은 물리, 데이터 링크, 네트워크, 전송, 세션, 표현, 응용 계층으로 구성된 통신 모델입니다.', '네트워크', '상',
        '물리, 데이터 링크, 네트워크, 전송, 세션, 표현, 응용'),
       ('캐시 메모리란?', '캐시 메모리는 CPU와 메인 메모리 사이에 위치한 고속 메모리로 지역성 원리를 활용하며 캐시 미스가 발생할 수 있습니다.', '컴퓨터구조', '중',
        '고속 메모리, CPU, 메인 메모리, 지역성, 캐시 미스'),
       ('파이프라이닝이란?', '파이프라이닝은 명령어 분할을 통해 병렬 처리 함으로써 처리량 증가를 달성하지만, 명령어 간의 충돌로 인해 해저드 문제도 발생할 수 있습니다.', '컴퓨터구조', '상',
        '명령어 분할, 병렬 처리, 처리량 증가, 해저드'),
       ('레지스터란?', '레지스터는 CPU 내부에 위치한 고속 저장 장치로, 데이터를 임시 저장 하고 산술 및 논리 연산에 사용됩니다.', '컴퓨터구조', '하',
        'CPU 내부, 고속 저장, 임시 저장, 연산'),
       ('RISC와 CISC의 차이는?', 'RISC는 단순 명령어를 빠르게 실행하고 파이프라인에 적합하며, CISC는 복잡 명령어를 통해 다양한 기능을 구현해 성능을 높입니다.', '컴퓨터구조', '중',
        '단순 명령어, 복잡 명령어, 파이프라인, 성능'),
       ('명령어 사이클이란?', '명령어 사이클은 CPU가 명령어를 인출, 해석, 실행 하는 반복 과정을 의미합니다.', '컴퓨터구조', '상', '인출, 해석, 실행, 반복 과정, CPU');

-- 3. 스트레치 콘텐츠 데이터 생성
INSERT INTO stretch_contents (title, description, media_type, media_url, created_at, updated_at)
VALUES
    ('목과 어깨 스트레칭', '장시간 코딩으로 인한 목과 어깨 긴장 완화를 위한 스트레칭', 'VIDEO', 'https://example.com/neck-shoulder-stretch.mp4', NOW() - INTERVAL 5 DAY, NOW()),
    ('손목 및 팔 스트레칭', '마우스와 키보드 사용으로 인한 손목 통증 예방 스트레칭', 'VIDEO', 'https://example.com/wrist-arm-stretch.mp4', NOW() - INTERVAL 4 DAY, NOW()),
    ('등과 허리 스트레칭', '장시간 앉아있기로 인한 등과 허리 피로 해소 스트레칭', 'IMAGE', 'https://example.com/back-stretch.jpg', NOW() - INTERVAL 3 DAY, NOW()),
    ('눈 운동', '모니터 응시로 인한 눈의 피로 완화를 위한 안구 운동', 'VIDEO', 'https://example.com/eye-exercise.mp4', NOW() - INTERVAL 2 DAY, NOW()),
    ('전신 스트레칭', '전체적인 몸의 긴장 완화를 위한 종합 스트레칭', 'VIDEO', 'https://example.com/full-body-stretch.mp4', NOW() - INTERVAL 1 DAY, NOW());

-- 4. 데일리 루틴 데이터 생성
-- 사용자 1: 프론트엔드 취준생 (2개 완료, 1개 진행중, 1개 쉬어가기)
INSERT INTO daily_routines (user_id, category, title, description, status, focus_time, actual_focus_time, break_time, started_at, completed_at, is_deleted, created_at, updated_at)
VALUES
    (1, '알고리즘', 'JavaScript 알고리즘 문제 풀기', '프로그래머스 Level 2 문제 3개 풀기', 'COMPLETED', 60, 60, 10, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 1 HOUR, false, NOW() - INTERVAL 1 DAY, NOW()),
    (1, 'HTML/CSS', '반응형 웹 디자인 학습', 'Media Query 활용 실습', 'COMPLETED', 45, 50, 5, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 4 HOUR, false, NOW() - INTERVAL 2 DAY, NOW()),
    (1, 'React', 'React Hooks 연습', 'useState, useEffect 활용한 미니 프로젝트', 'ACTIVE', 90, 30, 15, NOW() - INTERVAL 30 MINUTE, NULL, false, NOW(), NOW()),
    (1, 'JavaScript', 'ES6+ 문법 학습', 'Arrow Function, Destructuring 학습', 'PASS', 60, 0, 10, NULL, NULL, false, NOW() - INTERVAL 3 DAY, NOW());

-- 사용자 2: 백엔드 취준생 (3개 완료, 2개 진행중)
INSERT INTO daily_routines (user_id, category, title, description, status, focus_time, actual_focus_time, break_time, started_at, completed_at, is_deleted, created_at, updated_at)
VALUES
    (2, 'Java', 'Java 스트림 API 학습', '람다와 스트림 실습', 'COMPLETED', 70, 75, 10, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 2 HOUR, false, NOW() - INTERVAL 1 DAY, NOW()),
    (2, 'Spring', 'Spring Security 구현', '인증/인가 기능 구현 연습', 'COMPLETED', 120, 140, 20, NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 4 HOUR, false, NOW() - INTERVAL 2 DAY, NOW()),
    (2, 'DB', 'MySQL 인덱스 최적화', '쿼리 성능 개선 실습', 'COMPLETED', 60, 65, 5, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 23 HOUR, false, NOW() - INTERVAL 3 DAY, NOW()),
    (2, 'JPA', 'JPA 연관관계 매핑 학습', 'OneToMany, ManyToMany 실습', 'ACTIVE', 90, 25, 15, NOW() - INTERVAL 30 MINUTE, NULL, false, NOW(), NOW()),
    (2, '알고리즘', '그래프 알고리즘 구현', 'DFS/BFS 구현 연습', 'ACTIVE', 120, 0, 20, NULL, NULL, false, NOW(), NOW());

-- 사용자 3: 풀스택 개발자 (4개 완료, 1개 진행중, 1개 쉬어가기)
INSERT INTO daily_routines (user_id, category, title, description, status, focus_time, actual_focus_time, break_time, started_at, completed_at, is_deleted, created_at, updated_at)
VALUES
    (3, 'Node.js', 'Express 서버 구현', 'REST API 개발 실습', 'COMPLETED', 90, 90, 15, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 2.5 HOUR, false, NOW() - INTERVAL 1 DAY, NOW()),
    (3, 'React', '리액트 컴포넌트 설계', '재사용 가능한 컴포넌트 개발', 'COMPLETED', 75, 80, 10, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 6 HOUR, false, NOW() - INTERVAL 2 DAY, NOW()),
    (3, 'TypeScript', 'TypeScript 타입 정의', '제네릭 활용 실습', 'COMPLETED', 60, 60, 5, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 23 HOUR, false, NOW() - INTERVAL 3 DAY, NOW()),
    (3, 'Docker', '도커 컨테이너 배포', '멀티 컨테이너 애플리케이션 배포', 'COMPLETED', 120, 140, 20, NOW() - INTERVAL 2.5 DAY, NOW() - INTERVAL 2.3 DAY, false, NOW() - INTERVAL 4 DAY, NOW()),
    (3, 'CS', '네트워크 기초 학습', 'TCP/IP 프로토콜 공부', 'ACTIVE', 90, 15, 15, NOW() - INTERVAL 1 HOUR, NULL, false, NOW(), NOW()),
    (3, 'DB', 'NoSQL 학습', 'MongoDB 기본 CRUD 연습', 'PASS', 60, 0, 10, NULL, NULL, false, NOW() - INTERVAL 2 DAY, NOW());

-- 5. 포모도로 세션 데이터 생성
-- 사용자 1의 포모도로 세션
INSERT INTO pomodoro_sessions (routine_id, duration_minutes, started_at, ended_at)
VALUES
    (1, 25, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR + INTERVAL 25 MINUTE),
    (1, 25, NOW() - INTERVAL 1 HOUR - INTERVAL 30 MINUTE, NOW() - INTERVAL 1 HOUR - INTERVAL 5 MINUTE),
    (1, 10, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR + INTERVAL 10 MINUTE),
    (2, 25, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR + INTERVAL 25 MINUTE),
    (2, 25, NOW() - INTERVAL 4 HOUR - INTERVAL 30 MINUTE, NOW() - INTERVAL 4 HOUR - INTERVAL 5 MINUTE),
    (3, 25, NOW() - INTERVAL 30 MINUTE, NULL);

-- 사용자 2의 포모도로 세션
INSERT INTO pomodoro_sessions (routine_id, duration_minutes, started_at, ended_at)
VALUES
    (5, 25, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR + INTERVAL 25 MINUTE),
    (5, 25, NOW() - INTERVAL 2 HOUR - INTERVAL 30 MINUTE, NOW() - INTERVAL 2 HOUR - INTERVAL 5 MINUTE),
    (5, 20, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR + INTERVAL 20 MINUTE),
    (6, 25, NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR + INTERVAL 25 MINUTE),
    (6, 25, NOW() - INTERVAL 5 HOUR - INTERVAL 30 MINUTE, NOW() - INTERVAL 5 HOUR - INTERVAL 5 MINUTE),
    (6, 25, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR + INTERVAL 25 MINUTE),
    (6, 25, NOW() - INTERVAL 4 HOUR - INTERVAL 30 MINUTE, NOW() - INTERVAL 4 HOUR - INTERVAL 5 MINUTE),
    (6, 20, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR + INTERVAL 20 MINUTE),
    (7, 25, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY + INTERVAL 25 MINUTE),
    (7, 25, NOW() - INTERVAL 1 DAY + INTERVAL 30 MINUTE, NOW() - INTERVAL 1 DAY + INTERVAL 55 MINUTE),
    (7, 15, NOW() - INTERVAL 23 HOUR, NOW() - INTERVAL 23 HOUR + INTERVAL 15 MINUTE),
    (8, 25, NOW() - INTERVAL 30 MINUTE, NULL);

-- 사용자 3의 포모도로 세션
INSERT INTO pomodoro_sessions (routine_id, duration_minutes, started_at, ended_at)
VALUES
    (10, 25, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR + INTERVAL 25 MINUTE),
    (10, 25, NOW() - INTERVAL 3 HOUR - INTERVAL 30 MINUTE, NOW() - INTERVAL 3 HOUR - INTERVAL 5 MINUTE),
    (10, 25, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR + INTERVAL 25 MINUTE),
    (10, 15, NOW() - INTERVAL 2 HOUR - INTERVAL 30 MINUTE, NOW() - INTERVAL 2 HOUR - INTERVAL 15 MINUTE),
    (11, 25, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR + INTERVAL 25 MINUTE),
    (11, 25, NOW() - INTERVAL 6 HOUR - INTERVAL 30 MINUTE, NOW() - INTERVAL 6 HOUR - INTERVAL 5 MINUTE),
    (11, 30, NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR + INTERVAL 30 MINUTE),
    (12, 25, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY + INTERVAL 25 MINUTE),
    (12, 25, NOW() - INTERVAL 1 DAY + INTERVAL 30 MINUTE, NOW() - INTERVAL 1 DAY + INTERVAL 55 MINUTE),
    (12, 10, NOW() - INTERVAL 23 HOUR, NOW() - INTERVAL 23 HOUR + INTERVAL 10 MINUTE),
    (13, 25, NOW() - INTERVAL 2.5 DAY, NOW() - INTERVAL 2.5 DAY + INTERVAL 25 MINUTE),
    (13, 25, NOW() - INTERVAL 2.5 DAY + INTERVAL 30 MINUTE, NOW() - INTERVAL 2.5 DAY + INTERVAL 55 MINUTE),
    (13, 25, NOW() - INTERVAL 2.4 DAY, NOW() - INTERVAL 2.4 DAY + INTERVAL 25 MINUTE),
    (13, 25, NOW() - INTERVAL 2.4 DAY + INTERVAL 30 MINUTE, NOW() - INTERVAL 2.4 DAY + INTERVAL 55 MINUTE),
    (13, 40, NOW() - INTERVAL 2.3 DAY, NOW() - INTERVAL 2.3 DAY + INTERVAL 40 MINUTE),
    (14, 15, NOW() - INTERVAL 1 HOUR, NULL);

-- 6. Todo 항목 데이터 생성
-- 사용자 1의 Todo 항목
INSERT INTO todos (user_id, title, description, start_time, is_completed, completed_at, is_deleted, created_at, updated_at)
VALUES
    (1, '이력서 작성', '프론트엔드 개발자 포지션에 맞게 이력서 업데이트', NOW() - INTERVAL 2 DAY + INTERVAL 10 HOUR, true, NOW() - INTERVAL 2 DAY + INTERVAL 12 HOUR, false, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 2 DAY),
    (1, 'React 포트폴리오 개선', '프로젝트에 Redux 추가하고 README 업데이트', NOW() - INTERVAL 1 DAY + INTERVAL 14 HOUR, true, NOW() - INTERVAL 1 DAY + INTERVAL 16 HOUR, false, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY),
    (1, 'JavaScript 코딩 테스트 연습', '프로그래머스 Level 2 문제 5개 풀기', NOW() + INTERVAL 2 HOUR, false, NULL, false, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY),
    (1, 'CSS 프레임워크 비교 분석', 'Tailwind, Bootstrap, Material UI 장단점 정리', NOW() + INTERVAL 1 DAY + INTERVAL 10 HOUR, false, NULL, false, NOW(), NOW());

-- 사용자 2의 Todo 항목
INSERT INTO todos (user_id, title, description, start_time, is_completed, completed_at, is_deleted, created_at, updated_at)
VALUES
    (2, 'Spring 프로젝트 리팩토링', '컨트롤러 계층 개선 및 서비스 로직 분리', NOW() - INTERVAL 3 DAY + INTERVAL 9 HOUR, true, NOW() - INTERVAL 3 DAY + INTERVAL 11 HOUR, false, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 3 DAY),
    (2, 'SQL 성능 최적화', '인덱스 추가 및 쿼리 개선', NOW() - INTERVAL 1 DAY + INTERVAL 13 HOUR, true, NOW() - INTERVAL 1 DAY + INTERVAL 15 HOUR, false, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY),
    (2, 'JPA 연관관계 매핑 실습', 'OneToMany, ManyToMany 관계 구현 연습', NOW() - INTERVAL 5 HOUR, true, NOW() - INTERVAL 3 HOUR, false, NOW() - INTERVAL 1 DAY, NOW()),
    (2, '코딩 테스트 준비', '백준 알고리즘 문제 5개 풀기', NOW() + INTERVAL 3 HOUR, false, NULL, false, NOW(), NOW()),
    (2, '자기소개서 작성', '백엔드 개발자 포지션 지원용 자기소개서 작성', NOW() + INTERVAL 1 DAY + INTERVAL 9 HOUR, false, NULL, false, NOW(), NOW());

-- 사용자 3의 Todo 항목
INSERT INTO todos (user_id, title, description, start_time, is_completed, completed_at, is_deleted, created_at, updated_at)
VALUES
    (3, 'MERN 스택 프로젝트 시작', '풀스택 프로젝트 기본 구조 세팅', NOW() - INTERVAL 4 DAY + INTERVAL 10 HOUR, true, NOW() - INTERVAL 4 DAY + INTERVAL 12 HOUR, false, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 4 DAY),
    (3, 'AWS 배포 환경 구축', 'EC2, RDS 설정 및 CI/CD 파이프라인 구축', NOW() - INTERVAL 2 DAY + INTERVAL 14 HOUR, true, NOW() - INTERVAL 2 DAY + INTERVAL 16 HOUR, false, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 2 DAY),
    (3, 'TypeScript 리팩토링', '기존 JS 프로젝트 TypeScript로 변환', NOW() - INTERVAL 1 DAY + INTERVAL 9 HOUR, true, NOW() - INTERVAL 1 DAY + INTERVAL 11 HOUR, false, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY),
    (3, '기술 면접 준비', '네트워크/OS/DB 주요 개념 복습', NOW() + INTERVAL 2 HOUR, false, NULL, false, NOW() - INTERVAL 1 DAY, NOW()),
    (3, '포트폴리오 웹사이트 개선', '반응형 디자인 적용 및 프로젝트 추가', NOW() + INTERVAL 1 DAY + INTERVAL 13 HOUR, false, NULL, false, NOW(), NOW()),
    (3, '오픈 소스 기여하기', '관심 있는 GitHub 프로젝트에 PR 생성', NOW() + INTERVAL 2 DAY + INTERVAL 10 HOUR, false, NULL, false, NOW(), NOW());

-- 7. 사용자 답변 데이터 생성
INSERT INTO user_answers (user_id, question_id, answer_text, answered_at)
VALUES
    (1, 1, '퀵 정렬은 피벗을 기준으로 배열을 분할하고 재귀적으로 정렬하는 알고리즘입니다. 평균 O(n log n), 최악의 경우 O(n²)의 시간복잡도를 가집니다.', NOW() - INTERVAL 3 DAY),
    (1, 6, '스택은 LIFO(후입선출) 구조로 push/pop 연산을 지원하고, 큐는 FIFO(선입선출) 구조로 enqueue/dequeue 연산을 지원합니다.', NOW() - INTERVAL 2 DAY),
    (2, 11, '정규화는 데이터의 중복을 최소화하고 무결성을 보장하기 위한 과정입니다. 1NF, 2NF, 3NF 등의 단계가 있습니다.', NOW() - INTERVAL 1 DAY),
    (2, 12, 'ACID는 원자성(Atomicity), 일관성(Consistency), 고립성(Isolation), 지속성(Durability)을 의미하며 트랜잭션의 안전성을 보장합니다.', NOW() - INTERVAL 6 HOUR),
    (3, 2, 'DFS는 스택이나 재귀를 사용하여 깊이를 우선으로 탐색하고, BFS는 큐를 사용하여 너비를 우선으로 탐색합니다.', NOW() - INTERVAL 4 HOUR);

-- 8. 채용 공고 데이터 생성
INSERT INTO job_posting (user_id, title, site, url, due_date, last_checked_date, is_target, is_deleted)
VALUES
    (1, '카카오 프론트엔드 개발자', '카카오 채용', 'https://careers.kakao.com/jobs/P-12345', NOW() + INTERVAL 10 DAY, NOW() - INTERVAL 1 DAY, true, false),
    (1, '네이버 웹 개발자', '네이버 채용', 'https://recruit.navercorp.com/naver/job/detail/developer', NOW() + INTERVAL 15 DAY, NOW() - INTERVAL 2 DAY, false, false),
    (1, '토스 프론트엔드 엔지니어', '토스 채용', 'https://toss.im/career/job-detail/4567', NOW() + INTERVAL 7 DAY, NOW(), true, false),
    (2, '우아한형제들 백엔드 개발자', '우아한형제들', 'https://woowabros.com/recruit/backend-developer', NOW() + INTERVAL 12 DAY, NOW() - INTERVAL 1 DAY, true, false),
    (2, '쿠팡 서버 개발자', '쿠팡', 'https://www.coupang.jobs/kr/jobs/back-end-developer', NOW() + INTERVAL 20 DAY, NOW() - INTERVAL 3 DAY, false, false),
    (2, 'NHN 백엔드 엔지니어', 'NHN', 'https://recruit.nhn.com/kor/job/backend', NOW() + INTERVAL 8 DAY, NOW(), true, false),
    (3, '라인 풀스택 개발자', '라인 채용', 'https://careers.linecorp.com/ko/jobs/1234567890', NOW() + INTERVAL 18 DAY, NOW() - INTERVAL 1 DAY, true, false),
    (3, '당근마켓 개발자', '당근마켓', 'https://team.daangn.com/jobs/4567890123', NOW() + INTERVAL 25 DAY, NOW() - INTERVAL 4 DAY, false, false);

-- 9. 사용자 통계 데이터 생성
INSERT INTO user_stats (user_id, total_focus_minutes, daily_completion_rate, data_collected_at)
VALUES
    (1, 950, '{"2025-06-10": 80, "2025-06-11": 75, "2025-06-12": 90, "2025-06-13": 85, "2025-06-14": 70, "2025-06-15": 60}', NOW()),
    (2, 1420, '{"2025-06-10": 95, "2025-06-11": 90, "2025-06-12": 85, "2025-06-13": 92, "2025-06-14": 88, "2025-06-15": 75}', NOW()),
    (3, 1850, '{"2025-06-10": 85, "2025-06-11": 92, "2025-06-12": 88, "2025-06-13": 90, "2025-06-14": 95, "2025-06-15": 80}', NOW());

-- 10. 사용자 로그인 로그 데이터 생성
INSERT INTO user_login_logs (user_id, login_time, ip_address)
VALUES
    (1, NOW() - INTERVAL 30 MINUTE, '192.168.1.100'),
    (1, NOW() - INTERVAL 1 DAY, '192.168.1.100'),
    (1, NOW() - INTERVAL 2 DAY, '10.0.0.50'),
    (2, NOW() - INTERVAL 1 HOUR, '172.16.0.25'),
    (2, NOW() - INTERVAL 1 DAY, '172.16.0.25'),
    (2, NOW() - INTERVAL 3 DAY, '192.168.0.10'),
    (3, NOW() - INTERVAL 2 HOUR, '203.249.12.34'),
    (3, NOW() - INTERVAL 6 HOUR, '203.249.12.34'),
    (3, NOW() - INTERVAL 1 DAY, '121.78.45.67'),
    (4, NOW() - INTERVAL 5 DAY, '10.1.1.1');

-- 11. 코딩 테스트 리뷰 데이터 생성
INSERT INTO coding_test_reviews (routine_id, problem_title, problem_description, my_solution, correct_solution, problem_type, is_deleted, created_at, updated_at)
VALUES
    (1, '두 정수 사이의 합', '두 정수 a, b가 주어졌을 때 a와 b 사이에 속한 모든 정수의 합을 리턴하는 함수 solution을 완성하세요.',
     'function solution(a, b) {\n    let sum = 0;\n    for(let i = Math.min(a,b); i <= Math.max(a,b); i++) {\n        sum += i;\n    }\n    return sum;\n}',
     'function solution(a, b) {\n    return (Math.abs(a-b)+1) * (a+b) / 2;\n}',
     '수학', false, NOW() - INTERVAL 1 DAY, NOW()),

    (5, '최대공약수와 최소공배수', '두 수를 입력받아 두 수의 최대공약수와 최소공배수를 반환하는 함수 solution을 완성해 보세요.',
     'function solution(n, m) {\n    const gcd = (a, b) => b === 0 ? a : gcd(b, a % b);\n    const g = gcd(n, m);\n    return [g, n * m / g];\n}',
     'function solution(n, m) {\n    const gcd = (a, b) => b === 0 ? a : gcd(b, a % b);\n    const g = gcd(n, m);\n    return [g, n * m / g];\n}',
     '수학', false, NOW() - INTERVAL 2 DAY, NOW()),

    (10, '타겟 넘버', 'n개의 음이 아닌 정수들이 있습니다. 이 정수들을 순서를 바꾸지 않고 적절히 더하거나 빼서 타겟 넘버를 만들려고 합니다.',
     'function solution(numbers, target) {\n    let count = 0;\n    function dfs(index, sum) {\n        if(index === numbers.length) {\n            if(sum === target) count++;\n            return;\n        }\n        dfs(index + 1, sum + numbers[index]);\n        dfs(index + 1, sum - numbers[index]);\n    }\n    dfs(0, 0);\n    return count;\n}',
     'function solution(numbers, target) {\n    let count = 0;\n    function dfs(index, sum) {\n        if(index === numbers.length) {\n            if(sum === target) count++;\n            return;\n        }\n        dfs(index + 1, sum + numbers[index]);\n        dfs(index + 1, sum - numbers[index]);\n    }\n    dfs(0, 0);\n    return count;\n}',
     'DFS', false, NOW() - INTERVAL 1 DAY, NOW());

-- 12. 면접 리뷰 데이터 생성
INSERT INTO interview_reviews (routine_id, tech_category, study_content, learned_concepts, difficult_parts, next_study_plan, is_deleted, created_at, updated_at)
VALUES
    (2, 'JavaScript', 'ES6+ 문법 중심으로 Arrow Function과 Destructuring 학습',
     '화살표 함수의 this 바인딩, 배열/객체 구조분해할당, 템플릿 리터럴',
     '화살표 함수에서 this가 상위 스코프를 참조하는 부분이 헷갈렸음',
     '클래스 문법과 모듈 시스템(import/export) 학습 예정',
     false, NOW() - INTERVAL 2 DAY, NOW()),

    (6, 'Spring Security', 'Spring Security를 이용한 인증/인가 구현 방법 학습',
     'JWT 토큰 기반 인증, Role 기반 권한 관리, Security Filter Chain',
     'JWT 토큰 갱신 로직과 예외 처리 부분이 복잡했음',
     'OAuth 2.0과 소셜 로그인 구현 방법 학습',
     false, NOW() - INTERVAL 1 DAY, NOW()),

    (11, 'React', '재사용 가능한 컴포넌트 설계 방법론 학습',
     'Custom Hook 작성법, 컴포넌트 합성, Props 인터페이스 설계',
     'Props drilling 문제와 Context API 활용 시점 판단',
     'Redux Toolkit 도입하여 전역 상태 관리 개선',
     false, NOW() - INTERVAL 2 DAY, NOW());

-- 외래키 제약조건 체크 재활성화
SET FOREIGN_KEY_CHECKS = 1;