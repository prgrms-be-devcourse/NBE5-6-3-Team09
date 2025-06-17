package com.codemap.core.routine.repository;

import com.codemap.core.routine.domain.DailyRoutine;
import com.codemap.core.routine.domain.InterviewReview;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterviewReviewRepository extends JpaRepository<InterviewReview, Long> {

    List<InterviewReview> findByRoutineAndIsDeletedFalse(DailyRoutine routine);

    @Query("SELECT i FROM InterviewReview i WHERE i.routine.id = :routineId AND i.isDeleted = false")
    List<InterviewReview> findByRoutineIdAndIsDeletedFalse(@Param("routineId") Long routineId);

    @Modifying
    @Query("DELETE FROM InterviewReview i WHERE i.routine.id IN (SELECT r.id FROM DailyRoutine r WHERE r.user.id = :userId)")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE InterviewReview i SET i.isDeleted = true WHERE i.routine.id = :routineId")
    void softDeleteByRoutineId(@Param("routineId") Long routineId);
}