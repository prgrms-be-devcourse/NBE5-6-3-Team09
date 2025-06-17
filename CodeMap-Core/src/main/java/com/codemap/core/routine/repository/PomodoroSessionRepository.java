package com.codemap.core.routine.repository;

import com.codemap.core.routine.domain.DailyRoutine;
import com.codemap.core.routine.domain.PomodoroSession;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PomodoroSessionRepository extends JpaRepository<PomodoroSession, Long> {

    @Query("SELECT ps FROM PomodoroSession ps WHERE ps.routine.id = :routineId ORDER BY ps.startedAt DESC")
    List<PomodoroSession> findByRoutineIdOrderByStartedAtDesc(@Param("routineId") Long routineId);

    @Query("SELECT COALESCE(SUM(COALESCE(ps.durationMinutes, 0)), 0) " +
        "FROM PomodoroSession ps WHERE ps.routine.id = :routineId")
    Integer getTotalSessionMinutesByRoutineId(@Param("routineId") Long routineId);

    // 실제 진행 시간을 초 단위로 계산하는 쿼리 (더 정확한 계산)
    @Query(value = """
        SELECT COALESCE(
            SUM(
                CASE 
                    WHEN ps.ended_at IS NOT NULL 
                    THEN TIMESTAMPDIFF(SECOND, ps.started_at, ps.ended_at)
                    ELSE 0 
                END
            ), 0
        ) as total_seconds
        FROM pomodoro_sessions ps 
        WHERE ps.routine_id = :routineId
        """, nativeQuery = true)
    Long getTotalActualSessionSeconds(@Param("routineId") Long routineId);

    @Modifying
    @Query("DELETE FROM PomodoroSession ps WHERE ps.routine.id IN (SELECT r.id FROM DailyRoutine r WHERE r.user.id = :userId)")
    void deleteByUserId(@Param("userId") Long userId);

    // 🔧 추가된 메서드들
    @Query("SELECT ps FROM PomodoroSession ps JOIN FETCH ps.routine r JOIN FETCH r.user WHERE ps.routine = :routine AND ps.endedAt IS NULL")
    List<PomodoroSession> findByRoutineAndEndedAtIsNull(@Param("routine") DailyRoutine routine);

    @Query("SELECT ps FROM PomodoroSession ps JOIN FETCH ps.routine r JOIN FETCH r.user WHERE ps.routine.id = :routineId AND ps.endedAt IS NULL")
    List<PomodoroSession> findByRoutineIdAndEndedAtIsNull(@Param("routineId") Long routineId);
}