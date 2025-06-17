package com.codemap.core.jobposting.repository;

import com.codemap.core.jobposting.domain.JobPosting;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    // ✅ 사용자의 모든 목표기업 조회 (삭제되지 않은 것만)
    List<JobPosting> findByUser_IdAndIsDeletedFalse(Long userId);

    // ✅ 특정 사용자의 목표기업 단건 조회 (삭제되지 않은 것만)
    Optional<JobPosting> findByIdAndUser_IdAndIsDeletedFalse(Long id, Long userId);

    // ✅ 사용자의 목표기업 목록 조회 (삭제되지 않은 것만)
    List<JobPosting> findByUser_IdAndIsTargetTrueAndIsDeletedFalse(Long userId);

    // ✅ 특정 사용자의 목표기업 개수 조회
    @Query("SELECT COUNT(jp) FROM JobPosting jp WHERE jp.user.id = :userId AND jp.isTarget = true AND jp.isDeleted = false")
    long countTargetJobPostingsByUserId(@Param("userId") Long userId);
}