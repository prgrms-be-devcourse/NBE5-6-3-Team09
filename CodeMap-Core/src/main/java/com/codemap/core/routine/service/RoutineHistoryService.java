package com.codemap.core.routine.service;

import com.codemap.core.routine.domain.CodingTestReview;
import com.codemap.core.routine.domain.CodingTestReviewHistory;
import com.codemap.core.routine.domain.DailyRoutine;
import com.codemap.core.routine.domain.InterviewReview;
import com.codemap.core.routine.domain.InterviewReviewHistory;
import com.codemap.core.routine.domain.PomodoroSession;
import com.codemap.core.routine.domain.PomodoroSessionHistory;
import com.codemap.core.routine.domain.RoutineHistory;
import com.codemap.core.routine.dto.CodingTestReviewHistoryDto;
import com.codemap.core.routine.dto.InterviewReviewHistoryDto;
import com.codemap.core.routine.dto.PomodoroSessionHistoryDto;
import com.codemap.core.routine.dto.RoutineHistoryDto;
import com.codemap.core.routine.repository.CodingTestReviewHistoryRepository;
import com.codemap.core.routine.repository.CodingTestReviewRepository;
import com.codemap.core.routine.repository.DailyRoutineRepository;
import com.codemap.core.routine.repository.InterviewReviewHistoryRepository;
import com.codemap.core.routine.repository.InterviewReviewRepository;
import com.codemap.core.routine.repository.PomodoroSessionHistoryRepository;
import com.codemap.core.routine.repository.PomodoroSessionRepository;
import com.codemap.core.routine.repository.RoutineHistoryRepository;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutineHistoryService {

    private final RoutineHistoryRepository routineHistoryRepository;
    private final DailyRoutineRepository dailyRoutineRepository;
    private final PomodoroSessionRepository pomodoroSessionRepository;
    private final PomodoroSessionHistoryRepository pomodoroSessionHistoryRepository;
    private final CodingTestReviewHistoryRepository codingTestReviewHistoryRepository;
    private final InterviewReviewHistoryRepository interviewReviewHistoryRepository;

    private final CodingTestReviewRepository codingTestReviewRepository;
    private final InterviewReviewRepository interviewReviewRepository;

    private final UserService userService;

    // 특정 날짜의 히스토리 조회
    public List<RoutineHistoryDto> getHistoryByDate(Long userId, LocalDate date) {
        User user = userService.getUserById(userId);
        List<RoutineHistory> histories = routineHistoryRepository.findByUserAndRoutineDateOrderByCreatedAtDesc(
            user, date);

        return histories.stream()
            .map(RoutineHistoryDto::fromEntity)
            .collect(Collectors.toList());
    }

    public List<CodingTestReviewHistoryDto> getCodingTestReviewHistoryByDate(Long userId,
        LocalDate date) {
        User user = userService.getUserById(userId);
        List<CodingTestReviewHistory> histories = codingTestReviewHistoryRepository
            .findByUserAndRoutineDateOrderByCreatedAtDesc(user, date);

        return histories.stream()
            .map(CodingTestReviewHistoryDto::fromEntity)
            .collect(Collectors.toList());
    }

    public List<InterviewReviewHistoryDto> getInterviewReviewHistoryByDate(Long userId,
        LocalDate date) {
        User user = userService.getUserById(userId);
        List<InterviewReviewHistory> histories = interviewReviewHistoryRepository
            .findByUserAndRoutineDateOrderByCreatedAtDesc(user, date);

        return histories.stream()
            .map(InterviewReviewHistoryDto::fromEntity)
            .collect(Collectors.toList());
    }

    public List<CodingTestReviewHistoryDto> getCodingTestReviewHistoryByRoutineId(Long routineId,
        LocalDate date) {
        List<CodingTestReviewHistory> histories = codingTestReviewHistoryRepository
            .findByOriginalRoutineIdAndRoutineDate(routineId, date);

        return histories.stream()
            .map(CodingTestReviewHistoryDto::fromEntity)
            .collect(Collectors.toList());
    }

    public List<InterviewReviewHistoryDto> getInterviewReviewHistoryByRoutineId(Long routineId,
        LocalDate date) {
        List<InterviewReviewHistory> histories = interviewReviewHistoryRepository
            .findByOriginalRoutineIdAndRoutineDate(routineId, date);

        return histories.stream()
            .map(InterviewReviewHistoryDto::fromEntity)
            .collect(Collectors.toList());
    }

    // 포모도로 세션 히스토리 조회 메서드들 추가
    public List<PomodoroSessionHistoryDto> getPomodoroSessionHistoryByDate(Long userId,
        LocalDate date) {
        User user = userService.getUserById(userId);
        List<PomodoroSessionHistory> histories = pomodoroSessionHistoryRepository
            .findByUserAndRoutineDateOrderByStartedAtDesc(user, date);

        return histories.stream()
            .map(PomodoroSessionHistoryDto::fromEntity)
            .collect(Collectors.toList());
    }

    public List<PomodoroSessionHistoryDto> getPomodoroSessionHistoryByRoutineId(Long routineId,
        LocalDate date) {
        List<PomodoroSessionHistory> histories = pomodoroSessionHistoryRepository
            .findByOriginalRoutineIdAndRoutineDate(routineId, date);

        return histories.stream()
            .map(PomodoroSessionHistoryDto::fromEntity)
            .collect(Collectors.toList());
    }

    // 날짜 범위의 히스토리 조회
    public List<RoutineHistoryDto> getHistoryByDateRange(Long userId, LocalDate startDate,
        LocalDate endDate) {
        User user = userService.getUserById(userId);
        List<RoutineHistory> histories = routineHistoryRepository.findByUserAndDateRange(user,
            startDate, endDate);

        return histories.stream()
            .map(RoutineHistoryDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public void archiveRoutinesAndCreateNew(LocalDate targetDate) {
        log.info("Starting routine archiving process for date: {}", targetDate);

        List<DailyRoutine> allRoutines = dailyRoutineRepository.findAllWithUser()
            .stream()
            .filter(routine -> !routine.getIsDeleted())
            .collect(Collectors.toList());

        log.info("Found {} routines to archive", allRoutines.size());

        for (DailyRoutine routine : allRoutines) {
            try {
                User user = userService.getUserById(routine.getUser().getId());
                Long routineId = routine.getId();

                // 🔧 1. 먼저 포모도로 세션 삭제 (FK 제약조건 해결)
                archivePomodoroSessions(routine, targetDate);

                // 🔧 2. 관련된 모든 자식 데이터들을 히스토리로 이관
                archiveReviewData(routine, user, targetDate);

                // 🔧 3. 루틴 히스토리 저장
                RoutineHistory history = createRoutineHistory(routine, user, targetDate);
                routineHistoryRepository.save(history);
                log.debug("Archived routine ID {} for user {}", routineId, user.getId());

                // 🔧 4. 새로운 루틴 생성
                DailyRoutine newRoutine = DailyRoutine.builder()
                    .user(user)
                    .category(routine.getCategory())
                    .title(routine.getTitle())
                    .description(routine.getDescription())
                    .focusTime(routine.getFocusTime())
                    .breakTime(routine.getBreakTime())
                    .status("ACTIVE")
                    .actualFocusTime(0)
                    .startedAt(null)
                    .completedAt(null)
                    .isDeleted(false)
                    .build();
                dailyRoutineRepository.save(newRoutine);

                // 🔧 5. 마지막에 기존 루틴 삭제 (이제 FK 제약조건 에러 없음!)
                dailyRoutineRepository.delete(routine);
                log.debug("Created new routine for user {}", user.getId());

            } catch (Exception e) {
                log.error("Error archiving routine ID {} for user {}: {}",
                    routine.getId(), routine.getUser().getId(), e.getMessage());
                throw new RuntimeException("Failed to archive routine: " + routine.getId(), e);
            }
        }

        log.info("Completed routine archiving process for date: {}", targetDate);
    }

    private void archiveReviewData(DailyRoutine routine, User user, LocalDate targetDate) {
        Long routineId = routine.getId();

        try {
            // 코딩테스트 리뷰를 히스토리로 이관
            List<CodingTestReview> codingTestReviews = codingTestReviewRepository
                .findByRoutineAndIsDeletedFalse(routine);

            for (CodingTestReview review : codingTestReviews) {
                CodingTestReviewHistory history = CodingTestReviewHistory
                    .fromCodingTestReview(review, user, targetDate);
                codingTestReviewHistoryRepository.save(history);
            }

            if (!codingTestReviews.isEmpty()) {
                codingTestReviewRepository.deleteAll(codingTestReviews);
                log.debug("Archived {} coding test reviews for routine {}",
                    codingTestReviews.size(), routineId);
            }

            // 면접 리뷰를 히스토리로 이관
            List<InterviewReview> interviewReviews = interviewReviewRepository
                .findByRoutineAndIsDeletedFalse(routine);

            for (InterviewReview review : interviewReviews) {
                InterviewReviewHistory history = InterviewReviewHistory
                    .fromInterviewReview(review, user, targetDate);
                interviewReviewHistoryRepository.save(history);
            }

            if (!interviewReviews.isEmpty()) {
                interviewReviewRepository.deleteAll(interviewReviews);
                log.debug("Archived {} interview reviews for routine {}", interviewReviews.size(),
                    routineId);
            }

        } catch (Exception e) {
            log.error("Error archiving review data for routine {}: {}", routineId, e.getMessage());
            throw e;
        }
    }

    private void archivePomodoroSessions(DailyRoutine routine, LocalDate targetDate) {
        try {
            User user = routine.getUser();

            // 🔧 1. 먼저 열린 세션들을 닫기
            List<PomodoroSession> openSessions = pomodoroSessionRepository
                .findByRoutineAndEndedAtIsNull(routine);

            for (PomodoroSession session : openSessions) {
                PomodoroSession closedSession = PomodoroSession.builder()
                    .id(session.getId())
                    .routine(session.getRoutine())
                    .durationMinutes(session.getDurationMinutes())
                    .startedAt(session.getStartedAt())
                    .endedAt(targetDate.atTime(23, 59, 59))
                    .build();
                pomodoroSessionRepository.save(closedSession);
            }

            if (!openSessions.isEmpty()) {
                log.debug("Closed {} open pomodoro sessions for routine {}", openSessions.size(),
                    routine.getId());
            }

            // 🔧 2. 해당 루틴의 모든 포모도로 세션을 히스토리로 이관
            List<PomodoroSession> allSessions = pomodoroSessionRepository
                .findByRoutineIdOrderByStartedAtDesc(routine.getId());

            if (!allSessions.isEmpty()) {
                // 히스토리 테이블로 데이터 이관
                for (PomodoroSession session : allSessions) {
                    PomodoroSessionHistory history = PomodoroSessionHistory
                        .fromPomodoroSession(session, user, targetDate);
                    pomodoroSessionHistoryRepository.save(history);
                }

                // 원본 세션 삭제
                pomodoroSessionRepository.deleteAll(allSessions);

                log.debug("Archived {} pomodoro sessions to history for routine {}",
                    allSessions.size(), routine.getId());
            }

        } catch (Exception e) {
            log.error("Error archiving pomodoro sessions for routine {}: {}", routine.getId(),
                e.getMessage());
            throw e;
        }
    }

    private RoutineHistory createRoutineHistory(DailyRoutine routine, User user,
        LocalDate targetDate) {
        return RoutineHistory.builder()
            .user(user)
            .originalRoutineId(routine.getId())
            .category(routine.getCategory())
            .title(routine.getTitle())
            .description(routine.getDescription())
            .status(routine.getStatus())
            .focusTime(routine.getFocusTime())
            .actualFocusTime(routine.getActualFocusTime())
            .breakTime(routine.getBreakTime())
            .startedAt(routine.getStartedAt())
            .completedAt(routine.getCompletedAt())
            .routineDate(targetDate)
            .createdAt(routine.getCreatedAt())
            .build();
    }

    // 특정 사용자의 루틴 통계 조회
    public Object getRoutineStats(Long userId) {
        User user = userService.getUserById(userId);
        return routineHistoryRepository.getStatsByCategory(user);
    }

    // 포모도로 세션 히스토리 기반 통계 조회 메서드들
    public Integer getTotalFocusTimeFromHistory(Long userId) {
        User user = userService.getUserById(userId);
        return pomodoroSessionHistoryRepository.getTotalCompletedMinutesByUser(user);
    }

    public List<Object[]> getDailyFocusTimeFromHistory(Long userId) {
        User user = userService.getUserById(userId);
        return pomodoroSessionHistoryRepository.getDailyFocusTimeByUser(user);
    }

    public List<Object[]> getMonthlyFocusTimeFromHistory(Long userId) {
        User user = userService.getUserById(userId);
        return pomodoroSessionHistoryRepository.getMonthlyFocusTimeByUser(user);
    }

    public List<Object[]> getWeeklyAverageFocusTimeFromHistory(Long userId) {
        return pomodoroSessionHistoryRepository.getWeeklyAverageFocusTimeByUser(userId);
    }

    public List<Object[]> getFocusTimeStatsByCategory(Long userId) {
        User user = userService.getUserById(userId);
        return pomodoroSessionHistoryRepository.getFocusTimeStatsByCategory(user);
    }

    public List<Object[]> getCompletionRate(Long userId) {
        User user = userService.getUserById(userId);
        return pomodoroSessionHistoryRepository.getCompletionRateByUser(user);
    }

    // 오래된 히스토리 데이터 정리 (예: 6개월 이전 데이터 삭제)
    @Transactional
    public void cleanupOldHistories(int monthsToKeep) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(monthsToKeep);

        // 루틴 히스토리 정리
        routineHistoryRepository.deleteOldHistories(cutoffDate);

        // 포모도로 세션 히스토리 정리
        pomodoroSessionHistoryRepository.deleteOldHistories(cutoffDate);

        log.info("Cleaned up history data older than {}", cutoffDate);
    }

    @Transactional
    public void checkAndArchiveIfNeeded(LocalDate targetDate) {
        log.info("Checking archive status for date: {}", targetDate);

        // 오늘 날짜라면 아카이브하지 않음
        if (targetDate.equals(LocalDate.now())) {
            log.info("Target date is today, skipping archive");
            return;
        }

        // 미래 날짜라면 아카이브하지 않음
        if (targetDate.isAfter(LocalDate.now())) {
            log.info("Target date is in the future, skipping archive");
            return;
        }

        // 해당 날짜의 히스토리가 이미 존재하는지 확인
        List<RoutineHistory> allHistoriesForDate = routineHistoryRepository.findAll()
            .stream()
            .filter(h -> h.getRoutineDate().equals(targetDate))
            .collect(Collectors.toList());

        List<DailyRoutine> allActiveRoutines = dailyRoutineRepository.findAllWithUser()
            .stream()
            .filter(r -> !r.getIsDeleted())
            .collect(Collectors.toList());

        if (allHistoriesForDate.isEmpty() && !allActiveRoutines.isEmpty()) {
            log.info("No history found for date {} but active routines exist. Starting archive...",
                targetDate);
            archiveRoutinesAndCreateNew(targetDate);
        } else if (!allHistoriesForDate.isEmpty()) {
            log.info("History already exists for date: {}", targetDate);
        } else {
            log.info("No routines to archive for date: {}", targetDate);
        }
    }
}