package com.codemap.core.routine.repository;

import com.codemap.core.routine.domain.PomodoroSessionHistory;
import com.codemap.core.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PomodoroSessionHistoryRepository extends
    JpaRepository<PomodoroSessionHistory, Long> {

    // 특정 사용자의 특정 날짜에 대한 포모도로 세션 히스토리 조회
    List<PomodoroSessionHistory> findByUserAndRoutineDateOrderByStartedAtDesc(User user, LocalDate routineDate);

    // 특정 루틴 ID에 대한 포모도로 세션 히스토리 조회
    @Query("SELECT p FROM PomodoroSessionHistory p WHERE p.originalRoutineId = :routineId AND p.routineDate = :routineDate ORDER BY p.startedAt DESC")
    List<PomodoroSessionHistory> findByOriginalRoutineIdAndRoutineDate(@Param("routineId") Long routineId, @Param("routineDate") LocalDate routineDate);

    // 특정 사용자의 날짜 범위별 포모도로 세션 히스토리 조회
    @Query("SELECT p FROM PomodoroSessionHistory p WHERE p.user = :user AND p.routineDate BETWEEN :startDate AND :endDate ORDER BY p.routineDate DESC, p.startedAt DESC")
    List<PomodoroSessionHistory> findByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 특정 사용자의 총 집중 시간 조회 (완료된 세션만)
    @Query("SELECT COALESCE(SUM(p.durationMinutes), 0) FROM PomodoroSessionHistory p WHERE p.user = :user AND p.endedAt IS NOT NULL")
    Integer getTotalCompletedMinutesByUser(@Param("user") User user);

    // 특정 사용자의 날짜별 집중 시간 조회
    @Query("SELECT p.routineDate, COALESCE(SUM(p.durationMinutes), 0) FROM PomodoroSessionHistory p WHERE p.user = :user AND p.endedAt IS NOT NULL GROUP BY p.routineDate ORDER BY p.routineDate DESC")
    List<Object[]> getDailyFocusTimeByUser(@Param("user") User user);

    // 특정 사용자의 월별 집중 시간 조회
    @Query("SELECT YEAR(p.routineDate), MONTH(p.routineDate), COALESCE(SUM(p.durationMinutes), 0) FROM PomodoroSessionHistory p WHERE p.user = :user AND p.endedAt IS NOT NULL GROUP BY YEAR(p.routineDate), MONTH(p.routineDate) ORDER BY YEAR(p.routineDate) DESC, MONTH(p.routineDate) DESC")
    List<Object[]> getMonthlyFocusTimeByUser(@Param("user") User user);

    // 특정 사용자의 요일별 평균 집중 시간 조회
    @Query(value = """
        SELECT DAYOFWEEK(p.routine_date) as weekday, 
               COALESCE(AVG(p.duration_minutes), 0) as avg_minutes
        FROM pomodoro_sessions_history p 
        WHERE p.user_id = :userId AND p.ended_at IS NOT NULL 
        GROUP BY DAYOFWEEK(p.routine_date)
        ORDER BY weekday
    """, nativeQuery = true)
    List<Object[]> getWeeklyAverageFocusTimeByUser(@Param("userId") Long userId);

    // 특정 사용자의 루틴별 집중 시간 통계
    @Query("""
        SELECT rh.category, 
               COUNT(p), 
               COALESCE(SUM(p.durationMinutes), 0),
               COALESCE(AVG(p.durationMinutes), 0)
        FROM PomodoroSessionHistory p 
        JOIN RoutineHistory rh ON p.originalRoutineId = rh.originalRoutineId AND p.routineDate = rh.routineDate
        WHERE p.user = :user AND p.endedAt IS NOT NULL 
        GROUP BY rh.category
        ORDER BY SUM(p.durationMinutes) DESC
    """)
    List<Object[]> getFocusTimeStatsByCategory(@Param("user") User user);

    // 사용자별 히스토리 데이터 삭제 (사용자 탈퇴 시)
    void deleteByUser(User user);

    // 특정 날짜 이전의 오래된 히스토리 데이터 삭제 (데이터 정리용)
    @Query("DELETE FROM PomodoroSessionHistory p WHERE p.routineDate < :cutoffDate")
    void deleteOldHistories(@Param("cutoffDate") LocalDate cutoffDate);

    // 세션 완료율 조회
    @Query("SELECT COUNT(p), SUM(CASE WHEN p.endedAt IS NOT NULL THEN 1 ELSE 0 END) FROM PomodoroSessionHistory p WHERE p.user = :user")
    List<Object[]> getCompletionRateByUser(@Param("user") User user);
}