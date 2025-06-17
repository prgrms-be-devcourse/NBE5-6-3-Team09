package com.codemap.core.jobposting.service;

import com.codemap.core.jobposting.domain.JobPosting;
import com.codemap.core.jobposting.dto.JobPostingRequest;
import com.codemap.core.jobposting.repository.JobPostingRepository;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    /**
     * 목표기업 등록
     */
    public void createJobPosting(Long userId, JobPostingRequest request) {
        log.info("목표기업 등록 시작 - userId: {}, request: {}", userId, request.getCompanyName());

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. JobPosting 엔티티 생성
        JobPosting jobPosting = JobPosting.builder()
            .user(user)
            .title(request.getCompanyName())  // companyName을 title로 저장
            .dueDate(request.getDueDate())
            .isTarget(true)  // 목표기업으로 설정
            .isDeleted(false)  // 삭제되지 않은 상태
            .build();

        // 3. 저장
        jobPostingRepository.save(jobPosting);
        log.info("목표기업 등록 완료 - title: {}", request.getCompanyName());
    }

    /**
     * 사용자의 목표기업 목록 조회
     */
    @Transactional(readOnly = true)
    public List<JobPosting> getJobPostings(Long userId) {
        log.info("목표기업 목록 조회 - userId: {}", userId);
        return jobPostingRepository.findByUser_IdAndIsTargetTrueAndIsDeletedFalse(userId);
    }

    /**
     * 목표기업 삭제 (Soft Delete)
     */
    public void deleteJobPosting(Long jobPostingId, Long userId) {
        log.info("목표기업 삭제 시작 - jobPostingId: {}, userId: {}", jobPostingId, userId);

        JobPosting jobPosting = jobPostingRepository.findByIdAndUser_IdAndIsDeletedFalse(jobPostingId, userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 공고를 찾을 수 없습니다."));

        // Soft Delete
        jobPosting.setDeleted(true);
        jobPostingRepository.save(jobPosting);

        log.info("목표기업 삭제 완료 - title: {}", jobPosting.getTitle());
    }
}