package com.codemap.core.routine.repository;

import com.codemap.core.routine.domain.DailyRoutine;
import com.codemap.core.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface DailyRoutineRepository extends JpaRepository<DailyRoutine, Long>,
    DailyRoutineRepositoryCustom,
    QuerydslPredicateExecutor<DailyRoutine> {

    List<DailyRoutine> findByUserAndIsDeletedFalseOrderByCreatedAtDesc(User user);

    @Query("SELECT dr FROM DailyRoutine dr WHERE dr.user = :user AND dr.isDeleted = false AND dr.status = :status ORDER BY dr.createdAt DESC")
    List<DailyRoutine> findByUserAndStatusAndNotDeleted(@Param("user") User user,
        @Param("status") String status);

    /*@Query("SELECT COALESCE(SUM(COALESCE(dr.focusTime, 0)), 0) " +
        "FROM DailyRoutine dr WHERE dr.user = :user AND dr.status = 'COMPLETED' AND dr.isDeleted = false")
    Integer getTotalFocusTimeByUser(@Param("user") User user);*/

    @Query("SELECT COALESCE(SUM(COALESCE(ps.durationMinutes, 0)), 0) " +
        "FROM PomodoroSession ps " +
        "JOIN ps.routine dr " +
        "WHERE dr.user = :user AND dr.status = 'COMPLETED' AND dr.isDeleted = false")
    Integer getTotalFocusTimeByUser(@Param("user") User user);

    @Query("SELECT dr.category, COALESCE(FLOOR(SUM(ps.durationMinutes)), 0) " +
        "FROM PomodoroSession ps " +
        "JOIN ps.routine dr " +
        "WHERE dr.user = :user AND dr.status = 'COMPLETED' AND dr.isDeleted = false " +
        "GROUP BY dr.category")
    List<Object[]> getTotalFocusTimeByCategory(@Param("user") User user);

    // 전체 루틴 수
    @Query("SELECT COUNT(r) FROM DailyRoutine r WHERE r.user.id = :userId AND r.isDeleted = false")
    long countAllByUserId(@Param("userId") Long userId);

    // 완료된 루틴 수
    @Query("SELECT COUNT(r) FROM DailyRoutine r WHERE r.user.id = :userId AND r.status = 'COMPLETED' AND r.isDeleted = false")
    long countCompletedByUserId(@Param("userId") Long userId);

    // 카테고리별 완료 수 & 전체 수
    @Query("""
            SELECT r.category, 
                   SUM(CASE WHEN r.status = 'COMPLETED' THEN 1 ELSE 0 END),
                   COUNT(r)
            FROM DailyRoutine r
            WHERE r.user.id = :userId AND r.isDeleted = false
            GROUP BY r.category
        """)
    List<Object[]> countByCategory(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM DailyRoutine dr WHERE dr.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Query(value = """
        SELECT DAYOFWEEK(ps.started_at) AS weekday, 
               COALESCE(FLOOR(SUM(ps.duration_minutes)), 0) as total_minutes
        FROM pomodoro_sessions ps
        JOIN daily_routines r ON ps.routine_id = r.id
        WHERE r.user_id = :userId AND r.status = 'COMPLETED' AND r.is_deleted = false
        GROUP BY DAYOFWEEK(ps.started_at)
    """, nativeQuery = true)
    List<Object[]> sumFocusTimeGroupedByWeekday(@Param("userId") Long userId);



    // 실제 집중 시간 기반 총 시간 조회
    @Query("SELECT SUM(dr.actualFocusTime) FROM DailyRoutine dr WHERE dr.user = :user AND dr.status = 'COMPLETED' AND dr.isDeleted = false AND dr.actualFocusTime IS NOT NULL")
    Integer getTotalActualFocusTimeByUser(@Param("user") User user);

    // 실제 집중 시간 기반 카테고리별 시간 조회
    @Query("SELECT dr.category, SUM(dr.actualFocusTime) FROM DailyRoutine dr WHERE dr.user = :user AND dr.status = 'COMPLETED' AND dr.isDeleted = false AND dr.actualFocusTime IS NOT NULL GROUP BY dr.category")
    List<Object[]> getTotalActualFocusTimeByCategory(@Param("user") User user);

    // 실제 집중 시간 기반 요일별 시간 조회
    @Query(value = """
        SELECT DAYOFWEEK(r.created_at) AS weekday, SUM(r.actual_focus_time)
        FROM daily_routines r
        WHERE r.user_id = :userId AND r.status = 'COMPLETED' AND r.is_deleted = false AND r.actual_focus_time IS NOT NULL
        GROUP BY DAYOFWEEK(r.created_at)
    """, nativeQuery = true)
    List<Object[]> sumActualFocusTimeGroupedByWeekday(@Param("userId") Long userId);


    @Query("SELECT d FROM DailyRoutine d JOIN FETCH d.user WHERE d.isDeleted = false")
    List<DailyRoutine> findAllWithUser();

    @Query("SELECT d FROM DailyRoutine d JOIN FETCH d.user WHERE d.user.id = :userId AND d.isDeleted = false")
    List<DailyRoutine> findAllByUserIdWithUser(@Param("userId") Long userId);

    @Query("SELECT d FROM DailyRoutine d JOIN FETCH d.user WHERE DATE(d.createdAt) = :date AND d.isDeleted = false")
    List<DailyRoutine> findByDateWithUser(@Param("date") LocalDate date);
}