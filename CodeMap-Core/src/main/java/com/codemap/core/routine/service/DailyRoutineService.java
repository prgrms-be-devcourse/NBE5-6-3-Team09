package com.codemap.core.routine.service;

import com.codemap.core.routine.domain.CodingTestReview;
import com.codemap.core.routine.domain.DailyRoutine;
import com.codemap.core.routine.domain.InterviewReview;
import com.codemap.core.routine.domain.PomodoroSession;
import com.codemap.core.routine.dto.CodingTestReviewDto;
import com.codemap.core.routine.dto.DailyRoutineDto;
import com.codemap.core.routine.dto.InterviewReviewDto;
import com.codemap.core.routine.dto.PomodoroSessionDto;
import com.codemap.core.routine.repository.CodingTestReviewRepository;
import com.codemap.core.routine.repository.DailyRoutineRepository;
import com.codemap.core.routine.repository.InterviewReviewRepository;
import com.codemap.core.routine.repository.PomodoroSessionRepository;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.service.UserService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DailyRoutineService {

    private final DailyRoutineRepository dailyRoutineRepository;
    private final PomodoroSessionRepository pomodoroSessionRepository;
    private final CodingTestReviewRepository codingTestReviewRepository;
    private final InterviewReviewRepository interviewReviewRepository;
    private final UserService userService;

    // 사용자의 모든 활성 루틴 조회
    public List<DailyRoutineDto> getActiveRoutinesByUser(Long userId) {
        User user = userService.getUserById(userId);
        return dailyRoutineRepository.findActiveRoutinesByUser(user)
            .stream()
            .map(DailyRoutineDto::fromEntity)
            .collect(Collectors.toList());
    }

    // 사용자의 모든 완료된 루틴 조회
    public List<DailyRoutineDto> getCompletedRoutinesByUser(Long userId) {
        User user = userService.getUserById(userId);
        return dailyRoutineRepository.findCompletedRoutinesByUser(user)
            .stream()
            .map(DailyRoutineDto::fromEntity)
            .collect(Collectors.toList());
    }

    // ID로 루틴 단건 조회
    public DailyRoutineDto getRoutineById(Long routineId, Long userId) {
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증 - 요청한 사용자의 루틴인지 확인
        if (!routine.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to access this routine");
        }

        return DailyRoutineDto.fromEntity(routine);
    }

    // 루틴 추가
    @Transactional
    public DailyRoutineDto createRoutine(DailyRoutineDto routineDto, Long userId) {
        User user = userService.getUserById(userId);

        DailyRoutine routine = DailyRoutine.builder()
            .user(user)
            .category(routineDto.getCategory())
            .title(routineDto.getTitle())
            .description(routineDto.getDescription())
            .focusTime(routineDto.getFocusTime())
            .breakTime(routineDto.getBreakTime())
            .status("ACTIVE")
            .isDeleted(false)
            .build();

        DailyRoutine savedRoutine = dailyRoutineRepository.save(routine);
        return DailyRoutineDto.fromEntity(savedRoutine);
    }


    public Map<String, List<DailyRoutineDto>> getRoutinesByDate(Long userId, LocalDate date) {
        User user = userService.getUserById(userId);

        // 🔧 1단계: 디버깅 로그 추가
        log.info("=== getRoutinesByDate Debug ===");
        log.info("Requested date: {}", date);
        log.info("Current system date: {}", LocalDate.now());
        log.info("User ID: {}", userId);

        // 🔧 2단계: 오늘 날짜 조건 완전 제거
        // 모든 날짜에 대해 조회하도록 수정
        List<DailyRoutine> dateRoutines = dailyRoutineRepository.findRoutinesByUserAndDate(user, date);

        // 🔧 3단계: 조회된 루틴들 디버깅
        log.info("Found {} routines for date {}", dateRoutines.size(), date);
        for (DailyRoutine routine : dateRoutines) {
            log.info("Routine: id={}, title={}, createdAt={}, status={}",
                routine.getId(), routine.getTitle(), routine.getCreatedAt(), routine.getStatus());
        }

        Map<String, List<DailyRoutineDto>> result = new HashMap<>();

        // 조회된 루틴들을 상태별로 분류
        List<DailyRoutineDto> activeRoutines = dateRoutines.stream()
            .filter(r -> "ACTIVE".equals(r.getStatus()))
            .map(DailyRoutineDto::fromEntity)
            .collect(Collectors.toList());

        List<DailyRoutineDto> completedRoutines = dateRoutines.stream()
            .filter(r -> "COMPLETED".equals(r.getStatus()))
            .map(DailyRoutineDto::fromEntity)
            .collect(Collectors.toList());

        List<DailyRoutineDto> passedRoutines = dateRoutines.stream()
            .filter(r -> "PASS".equals(r.getStatus()))
            .map(DailyRoutineDto::fromEntity)
            .collect(Collectors.toList());

        result.put("active", activeRoutines);
        result.put("completed", completedRoutines);
        result.put("passed", passedRoutines);

        // 🔧 4단계: 결과 디버깅
        log.info("Result - Active: {}, Completed: {}, Passed: {}",
            activeRoutines.size(), completedRoutines.size(), passedRoutines.size());
        log.info("=== End Debug ===");

        return result;
    }



    // 루틴 수정
    @Transactional
    public DailyRoutineDto updateRoutine(Long routineId, DailyRoutineDto routineDto, Long userId) {
        User user = userService.getUserById(userId);
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to update this routine");
        }

        // 기존 루틴을 삭제하고 새로운 루틴 생성 (Immutable 패턴)
        DailyRoutine updatedRoutine = DailyRoutine.builder()
            .id(routine.getId())
            .user(user)
            .category(routineDto.getCategory())
            .title(routineDto.getTitle())
            .description(routineDto.getDescription())
            .focusTime(routineDto.getFocusTime())
            .breakTime(routineDto.getBreakTime())
            .status(routine.getStatus())
            .startedAt(routine.getStartedAt())
            .completedAt(routine.getCompletedAt())
            .isDeleted(routine.getIsDeleted())
            .createdAt(routine.getCreatedAt())
            .build();

        DailyRoutine saved = dailyRoutineRepository.save(updatedRoutine);
        return DailyRoutineDto.fromEntity(saved);
    }

    // 루틴 삭제 (소프트 딜리트)
    @Transactional
    public void deleteRoutine(Long routineId, Long userId) {
        User user = userService.getUserById(userId);
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to delete this routine");
        }

        // 소프트 딜리트 처리
        DailyRoutine deletedRoutine = DailyRoutine.builder()
            .id(routine.getId())
            .user(user)
            .category(routine.getCategory())
            .title(routine.getTitle())
            .description(routine.getDescription())
            .focusTime(routine.getFocusTime())
            .status(routine.getStatus())
            .startedAt(routine.getStartedAt())
            .completedAt(routine.getCompletedAt())
            .isDeleted(true) // 삭제 표시
            .createdAt(routine.getCreatedAt())
            .build();

        dailyRoutineRepository.save(deletedRoutine);
    }

    // 루틴 완료 처리
    @Transactional
    public DailyRoutineDto completeRoutine(Long routineId, Long userId) {
        User user = userService.getUserById(userId);
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to complete this routine");
        }

        DailyRoutine completedRoutine = DailyRoutine.builder()
            .id(routine.getId())
            .user(user)
            .category(routine.getCategory())
            .title(routine.getTitle())
            .description(routine.getDescription())
            .focusTime(routine.getFocusTime())
            .actualFocusTime(routine.getActualFocusTime())
            .breakTime(routine.getBreakTime())
            .status("COMPLETED") // 완료 상태로 변경
            .startedAt(routine.getStartedAt())
            .completedAt(LocalDateTime.now()) // 완료 시간 설정
            .isDeleted(routine.getIsDeleted())
            .createdAt(routine.getCreatedAt())
            .build();

        DailyRoutine saved = dailyRoutineRepository.save(completedRoutine);
        return DailyRoutineDto.fromEntity(saved);
    }

    // 루틴 PASS 처리 (쉬어가기)
    @Transactional
    public DailyRoutineDto passRoutine(Long routineId, Long userId) {
        User user = userService.getUserById(userId);
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to pass this routine");
        }

        DailyRoutine passedRoutine = DailyRoutine.builder()
            .id(routine.getId())
            .user(user)
            .category(routine.getCategory())
            .title(routine.getTitle())
            .description(routine.getDescription())
            .focusTime(routine.getFocusTime())
            .breakTime(routine.getBreakTime())
            .status("PASS") // PASS 상태로 변경
            .startedAt(routine.getStartedAt())
            .completedAt(routine.getCompletedAt())
            .isDeleted(routine.getIsDeleted())
            .createdAt(routine.getCreatedAt())
            .build();

        DailyRoutine saved = dailyRoutineRepository.save(passedRoutine);
        return DailyRoutineDto.fromEntity(saved);
    }

    // 루틴 완료 취소
    @Transactional
    public DailyRoutineDto cancelCompletion(Long routineId, Long userId) {
        User user = userService.getUserById(userId);
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to cancel this routine");
        }

        DailyRoutine activeRoutine = DailyRoutine.builder()
            .id(routine.getId())
            .user(user)
            .category(routine.getCategory())
            .title(routine.getTitle())
            .description(routine.getDescription())
            .focusTime(routine.getFocusTime())
            .breakTime(routine.getBreakTime())
            .status("ACTIVE") // 활성 상태로 되돌림
            .startedAt(routine.getStartedAt())
            .completedAt(null) // 완료 시간 제거
            .isDeleted(routine.getIsDeleted())
            .createdAt(routine.getCreatedAt())
            .build();

        DailyRoutine saved = dailyRoutineRepository.save(activeRoutine);
        return DailyRoutineDto.fromEntity(saved);
    }

    // 포모도로 세션 시작
    @Transactional
    public PomodoroSessionDto startPomodoroSession(Long routineId, Long userId) {
        User user = userService.getUserById(userId);
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to start pomodoro for this routine");
        }

        // 루틴 시작 시간 설정
        if (routine.getStartedAt() == null) {
            DailyRoutine startedRoutine = DailyRoutine.builder()
                .id(routine.getId())
                .user(user)
                .category(routine.getCategory())
                .title(routine.getTitle())
                .description(routine.getDescription())
                .focusTime(routine.getFocusTime())
                .breakTime(routine.getBreakTime())
                .status(routine.getStatus())
                .startedAt(LocalDateTime.now()) // 시작 시간 설정
                .completedAt(routine.getCompletedAt())
                .isDeleted(routine.getIsDeleted())
                .createdAt(routine.getCreatedAt())
                .build();

            dailyRoutineRepository.save(startedRoutine);
        }

        // 포모도로 세션 생성
        PomodoroSession session = PomodoroSession.builder()
            .routine(routine)
            .durationMinutes(25) // 기본 25분 설정 (변경 가능)
            .startedAt(LocalDateTime.now())
            .build();

        PomodoroSession savedSession = pomodoroSessionRepository.save(session);
        return PomodoroSessionDto.fromEntity(savedSession);
    }

    // 포모도로 세션 종료
    @Transactional
    public PomodoroSessionDto endPomodoroSession(Long sessionId, Long userId) {
        PomodoroSession session = pomodoroSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));

        // 권한 검증
        if (!session.getRoutine().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to end this session");
        }
        LocalDateTime endTime = LocalDateTime.now();

        long actualSeconds = Duration.between(session.getStartedAt(), endTime).getSeconds();
        int actualMinutes = actualSeconds < 60 ? 0 : (int) Math.ceil(actualSeconds / 60.0);

        PomodoroSession endedSession = PomodoroSession.builder()
            .id(session.getId())
            .routine(session.getRoutine())
            .durationMinutes(actualMinutes)
            .startedAt(session.getStartedAt())
            .endedAt(endTime)
            .build();

        PomodoroSession saved = pomodoroSessionRepository.save(endedSession);
        return PomodoroSessionDto.fromEntity(saved);
    }

    @Transactional
    public void updateActualFocusTime(Long routineId, int actualMinutes, Long userId) {
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to update this routine");
        }

        // 기존 실제 시간에 새 시간 누적 (여러 세션이 있을 수 있으므로)
        int currentActualTime = routine.getActualFocusTime() != null ? routine.getActualFocusTime() : 0;
        int newActualTime = currentActualTime + actualMinutes;

        DailyRoutine updatedRoutine = DailyRoutine.builder()
            .id(routine.getId())
            .user(routine.getUser())
            .category(routine.getCategory())
            .title(routine.getTitle())
            .description(routine.getDescription())
            .focusTime(routine.getFocusTime())
            .actualFocusTime(newActualTime) // 실제 시간 업데이트
            .breakTime(routine.getBreakTime())
            .status(routine.getStatus())
            .startedAt(routine.getStartedAt())
            .completedAt(routine.getCompletedAt())
            .isDeleted(routine.getIsDeleted())
            .createdAt(routine.getCreatedAt())
            .build();

        dailyRoutineRepository.save(updatedRoutine);
    }


    public List<DailyRoutineDto> getPassedRoutinesByUser(Long userId) {
        User user = userService.getUserById(userId);
        return dailyRoutineRepository.findPassedRoutinesByUser(user)
            .stream()
            .map(DailyRoutineDto::fromEntity)
            .collect(Collectors.toList());
    }

    public DailyRoutineDto getNextRoutine(Long currentRoutineId, Long userId) {
        User user = userService.getUserById(userId);
        DailyRoutine currentRoutine = dailyRoutineRepository.findById(currentRoutineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found"));

        // ACTIVE 상태의 루틴만 조회
        List<DailyRoutine> activeRoutines = dailyRoutineRepository.findByUserAndStatusAndNotDeleted(user, "ACTIVE");

        // 현재 루틴을 제외하고, ID 순서로 다음 루틴 찾기
        // (날짜가 바뀌면서 createdAt이 비슷해질 수 있으므로 ID로 정렬)
        return activeRoutines.stream()
            .filter(routine -> !routine.getId().equals(currentRoutineId))
            .filter(routine -> routine.getId() > currentRoutineId)
            .min((r1, r2) -> r1.getId().compareTo(r2.getId()))
            .map(DailyRoutineDto::fromEntity)
            .orElse(null);
    }

    // 코딩테스트 회고 저장
    @Transactional
    public List<CodingTestReviewDto> saveCodingTestReviews(Long routineId, Long userId, List<CodingTestReviewDto> reviewDtos) {
        User user = userService.getUserById(userId);
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to add reviews to this routine");
        }

        List<CodingTestReview> reviews = reviewDtos.stream()
            .map(dto -> CodingTestReview.builder()
                .routine(routine)
                .problemTitle(dto.getProblemTitle())
                .problemDescription(dto.getProblemDescription())
                .mySolution(dto.getMySolution())
                .correctSolution(dto.getCorrectSolution())
                .problemType(dto.getProblemType())
                .isDeleted(false)
                .build())
            .collect(Collectors.toList());

        List<CodingTestReview> savedReviews = codingTestReviewRepository.saveAll(reviews);

        return savedReviews.stream()
            .map(CodingTestReviewDto::fromEntity)
            .collect(Collectors.toList());
    }

    // 코딩테스트 회고 조회
    @Transactional(readOnly = true)
    public List<CodingTestReviewDto> getCodingTestReviews(Long routineId, Long userId) {
        User user = userService.getUserById(userId);
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to view reviews for this routine");
        }

        List<CodingTestReview> reviews = codingTestReviewRepository.findByRoutineAndIsDeletedFalse(routine);

        return reviews.stream()
            .map(CodingTestReviewDto::fromEntity)
            .collect(Collectors.toList());
    }

    // 코딩테스트 루틴인지 확인
    public boolean isCodingTestRoutine(DailyRoutineDto routine) {
        return routine.getCategory() != null && routine.getCategory().startsWith("코딩테스트 준비");
    }

    // 코딩테스트 회고와 함께 루틴 완료 처리
    @Transactional
    public DailyRoutineDto completeCodingTestRoutine(Long routineId, Long userId, List<CodingTestReviewDto> reviewDtos) {
        // 회고 저장
        saveCodingTestReviews(routineId, userId, reviewDtos);

        // 루틴 완료 처리
        return completeRoutine(routineId, userId);
    }

    // 면접준비 회고 저장
    @Transactional
    public List<InterviewReviewDto> saveInterviewReviews(Long routineId, Long userId, List<InterviewReviewDto> reviewDtos) {
        User user = userService.getUserById(userId);
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to add reviews to this routine");
        }

        List<InterviewReview> reviews = reviewDtos.stream()
            .map(dto -> InterviewReview.builder()
                .routine(routine)
                .techCategory(dto.getTechCategory())
                .studyContent(dto.getStudyContent())
                .learnedConcepts(dto.getLearnedConcepts())
                .difficultParts(dto.getDifficultParts())
                .nextStudyPlan(dto.getNextStudyPlan())
                .isDeleted(false)
                .build())
            .collect(Collectors.toList());

        List<InterviewReview> savedReviews = interviewReviewRepository.saveAll(reviews);

        return savedReviews.stream()
            .map(InterviewReviewDto::fromEntity)
            .collect(Collectors.toList());
    }

    // 면접준비 회고 조회
    @Transactional(readOnly = true)
    public List<InterviewReviewDto> getInterviewReviews(Long routineId, Long userId) {
        User user = userService.getUserById(userId);
        DailyRoutine routine = dailyRoutineRepository.findById(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        // 권한 검증
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to view reviews for this routine");
        }

        List<InterviewReview> reviews = interviewReviewRepository.findByRoutineAndIsDeletedFalse(routine);

        return reviews.stream()
            .map(InterviewReviewDto::fromEntity)
            .collect(Collectors.toList());
    }

    // 면접준비 루틴인지 확인
    public boolean isInterviewRoutine(DailyRoutineDto routine) {
        return routine.getCategory() != null && routine.getCategory().startsWith("면접준비");
    }

    // 면접준비 회고와 함께 루틴 완료 처리
    @Transactional
    public DailyRoutineDto completeInterviewRoutine(Long routineId, Long userId, List<InterviewReviewDto> reviewDtos) {
        // 회고 저장
        saveInterviewReviews(routineId, userId, reviewDtos);

        // 루틴 완료 처리
        return completeRoutine(routineId, userId);
    }

    public List<DailyRoutineDto> getAllActiveRoutinesByUser(Long userId) {
        User user = userService.getUserById(userId);
        List<DailyRoutine> routines = dailyRoutineRepository.findByUserAndIsDeletedFalseOrderByCreatedAtDesc(user);

        return routines.stream()
            .map(DailyRoutineDto::fromEntity)
            .collect(Collectors.toList());
    }
}