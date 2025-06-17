package com.codemap.core.routine.repository;

import com.codemap.core.routine.domain.RoutineHistory;
import com.codemap.core.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoutineHistoryRepository extends JpaRepository<RoutineHistory, Long> {

    // 특정 사용자의 특정 날짜 루틴 히스토리 조회
    List<RoutineHistory> findByUserAndRoutineDateOrderByCreatedAtDesc(User user, LocalDate routineDate);

    // 특정 사용자의 특정 날짜 범위 루틴 히스토리 조회
    @Query("SELECT rh FROM RoutineHistory rh WHERE rh.user = :user AND rh.routineDate BETWEEN :startDate AND :endDate ORDER BY rh.routineDate DESC, rh.createdAt DESC")
    List<RoutineHistory> findByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 특정 사용자의 월별 완료된 루틴 수 조회
    @Query("SELECT COUNT(rh) FROM RoutineHistory rh WHERE rh.user = :user AND rh.status = 'COMPLETED' AND YEAR(rh.routineDate) = :year AND MONTH(rh.routineDate) = :month")
    long countCompletedByUserAndYearMonth(@Param("user") User user, @Param("year") int year, @Param("month") int month);

    // 특정 사용자의 카테고리별 히스토리 통계
    @Query("SELECT rh.category, COUNT(rh), SUM(CASE WHEN rh.status = 'COMPLETED' THEN 1 ELSE 0 END) FROM RoutineHistory rh WHERE rh.user = :user GROUP BY rh.category")
    List<Object[]> getStatsByCategory(@Param("user") User user);

    // 사용자별 히스토리 데이터 삭제 (사용자 탈퇴 시)
    @Modifying
    @Query("DELETE FROM RoutineHistory rh WHERE rh.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // 특정 날짜 이전의 오래된 히스토리 데이터 삭제 (데이터 정리용)
    @Modifying
    @Query("DELETE FROM RoutineHistory rh WHERE rh.routineDate < :cutoffDate")
    void deleteOldHistories(@Param("cutoffDate") LocalDate cutoffDate);
}