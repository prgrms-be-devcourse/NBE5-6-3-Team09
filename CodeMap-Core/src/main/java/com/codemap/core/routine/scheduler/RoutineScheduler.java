package com.codemap.core.routine.scheduler;

import com.codemap.core.routine.service.RoutineHistoryService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutineScheduler {

    private final RoutineHistoryService routineHistoryService;

    /**
     * 매일 자정 1분에 실행 - 전날 루틴을 히스토리로 이관하고 새로운 루틴 생성
     */
    @Scheduled(cron = "0 1 0 * * ?") // 매일 00:01:00에 실행
    public void archiveDailyRoutines() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        log.info("=== Daily Routine Archive Job Started ===");
        log.info("Archiving routines for date: {}", yesterday);

        try {
            routineHistoryService.archiveRoutinesAndCreateNew(yesterday);
            log.info("=== Daily Routine Archive Job Completed Successfully ===");
        } catch (Exception e) {
            log.error("=== Daily Routine Archive Job Failed ===", e);
        }
    }

    /**
     * 매주 일요일 새벽 2시에 실행 - 6개월 이전 히스토리 데이터 정리
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    public void cleanupOldHistories() {
        log.info("=== Weekly History Cleanup Job Started ===");

        try {
            routineHistoryService.cleanupOldHistories(6); // 6개월 이전 데이터 삭제
            log.info("=== Weekly History Cleanup Job Completed Successfully ===");
        } catch (Exception e) {
            log.error("=== Weekly History Cleanup Job Failed ===", e);
        }
    }

    /**
     * 테스트용 - 수동으로 아카이브 실행할 수 있는 메서드
     */
    public void manualArchive(LocalDate targetDate) {
        log.info("=== Manual Archive Started for date: {} ===", targetDate);

        try {
            routineHistoryService.archiveRoutinesAndCreateNew(targetDate);
            log.info("=== Manual Archive Completed Successfully ===");
        } catch (Exception e) {
            log.error("=== Manual Archive Failed ===", e);
        }
    }
}