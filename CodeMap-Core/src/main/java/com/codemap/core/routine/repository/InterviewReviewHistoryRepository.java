package com.codemap.core.routine.repository;

import com.codemap.core.routine.domain.InterviewReviewHistory;
import com.codemap.core.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterviewReviewHistoryRepository extends
    JpaRepository<InterviewReviewHistory, Long> {

    // 특정 사용자의 특정 날짜에 대한 면접 리뷰 히스토리 조회
    List<InterviewReviewHistory> findByUserAndRoutineDateOrderByCreatedAtDesc(User user, LocalDate routineDate);

    // 특정 루틴 ID에 대한 면접 리뷰 히스토리 조회
    @Query("SELECT i FROM InterviewReviewHistory i WHERE i.originalRoutineId = :routineId AND i.routineDate = :routineDate")
    List<InterviewReviewHistory> findByOriginalRoutineIdAndRoutineDate(@Param("routineId") Long routineId, @Param("routineDate") LocalDate routineDate);

    // 사용자별 히스토리 데이터 삭제 (사용자 탈퇴 시)
    void deleteByUser(User user);
}