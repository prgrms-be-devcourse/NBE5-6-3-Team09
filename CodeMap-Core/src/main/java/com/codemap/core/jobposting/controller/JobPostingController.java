package com.codemap.core.jobposting.controller;

import com.codemap.core.jobposting.domain.JobPosting;
import com.codemap.core.jobposting.dto.JobPostingRequest;
import com.codemap.core.jobposting.dto.JobPostingResponse;
import com.codemap.core.jobposting.service.JobPostingService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobposting")
@RequiredArgsConstructor
@Slf4j
public class JobPostingController {

    private final JobPostingService jobPostingService;

    // ✅ 1. 목표기업 등록
    @PostMapping
    public ResponseEntity<Void> createJobPosting(@RequestBody JobPostingRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        log.info("🔥 POST /jobposting - session userId = {}, request = {}", userId, request);

        if (userId == null) {
            log.error("❌ 로그인되지 않은 사용자");
            return ResponseEntity.status(401).build();
        }

        try {
            jobPostingService.createJobPosting(userId, request);
            log.info("✅ 목표기업 등록 성공");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("❌ 목표기업 등록 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 2. 목표기업 목록 조회
    @GetMapping
    public ResponseEntity<List<JobPostingResponse>> getJobPostings(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        log.info("🔥 GET /jobposting - session userId = {}", userId);

        if (userId == null) {
            log.error("❌ 로그인되지 않은 사용자");
            return ResponseEntity.status(401).build();
        }

        try {
            List<JobPosting> postings = jobPostingService.getJobPostings(userId);
            List<JobPostingResponse> response = postings.stream()
                .map(JobPostingResponse::from)
                .toList();

            log.info("✅ 목표기업 목록 조회 성공: {} 개", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ 목표기업 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 3. 목표기업 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPosting(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        log.info("🔥 DELETE /jobposting/{} - session userId = {}", id, userId);

        if (userId == null) {
            log.error("❌ 로그인되지 않은 사용자");
            return ResponseEntity.status(401).build();
        }

        try {
            jobPostingService.deleteJobPosting(id, userId);
            log.info("✅ 목표기업 삭제 성공");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("❌ 목표기업 삭제 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}