package com.codemap.core.routine.repository;

import com.codemap.core.routine.domain.CodingTestReview;
import com.codemap.core.routine.domain.DailyRoutine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CodingTestReviewRepository extends JpaRepository<CodingTestReview, Long> {

    List<CodingTestReview> findByRoutineAndIsDeletedFalse(DailyRoutine routine);

    @Query("SELECT c FROM CodingTestReview c WHERE c.routine.id = :routineId AND c.isDeleted = false")
    List<CodingTestReview> findByRoutineIdAndIsDeletedFalse(@Param("routineId") Long routineId);

    @Modifying
    @Query("DELETE FROM CodingTestReview c WHERE c.routine.id IN (SELECT r.id FROM DailyRoutine r WHERE r.user.id = :userId)")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE CodingTestReview c SET c.isDeleted = true WHERE c.routine.id = :routineId")
    void softDeleteByRoutineId(@Param("routineId") Long routineId);
}