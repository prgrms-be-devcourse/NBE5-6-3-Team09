package com.codemap.core.routine.domain;

import com.codemap.core.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interview_reviews_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InterviewReviewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "original_review_id")
    private Long originalReviewId; // 원본 리뷰의 ID

    @Column(name = "original_routine_id")
    private Long originalRoutineId; // 원본 루틴의 ID

    private String techCategory; // 기술 분야

    @Column(columnDefinition = "TEXT")
    private String studyContent; // 오늘 공부한 내용

    @Column(columnDefinition = "TEXT")
    private String learnedConcepts; // 학습한 개념들

    @Column(columnDefinition = "TEXT")
    private String difficultParts; // 어려웠던 부분

    @Column(columnDefinition = "TEXT")
    private String nextStudyPlan; // 다음에 공부할 내용

    @Column(name = "routine_date")
    private LocalDate routineDate; // 해당 루틴이 수행된 날짜

    @Column(name = "archived_at")
    private LocalDateTime archivedAt; // 히스토리로 옮겨진 시간

    @Column(updatable = false)
    private LocalDateTime createdAt; // 원본 리뷰의 생성 시간

    private LocalDateTime updatedAt; // 원본 리뷰의 수정 시간

    @PrePersist
    protected void onCreate() {
        this.archivedAt = LocalDateTime.now();
    }

    // InterviewReview를 InterviewReviewHistory로 변환하는 정적 메서드
    public static InterviewReviewHistory fromInterviewReview(InterviewReview review, User user, LocalDate routineDate) {
        return InterviewReviewHistory.builder()
            .user(user)
            .originalReviewId(review.getId())
            .originalRoutineId(review.getRoutine().getId())
            .techCategory(review.getTechCategory())
            .studyContent(review.getStudyContent())
            .learnedConcepts(review.getLearnedConcepts())
            .difficultParts(review.getDifficultParts())
            .nextStudyPlan(review.getNextStudyPlan())
            .routineDate(routineDate)
            .createdAt(review.getCreatedAt())
            .updatedAt(review.getUpdatedAt())
            .build();
    }
}