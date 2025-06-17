package com.codemap.core.routine.dto;

import com.codemap.core.routine.domain.InterviewReview;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewReviewDto {
    private Long id;
    private Long routineId;
    private String techCategory; // 기술 분야
    private String studyContent; // 오늘 공부한 내용
    private String learnedConcepts; // 학습한 개념들
    private String difficultParts; // 어려웠던 부분
    private String nextStudyPlan; // 다음에 공부할 내용
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InterviewReviewDto fromEntity(InterviewReview entity) {
        return InterviewReviewDto.builder()
            .id(entity.getId())
            .routineId(entity.getRoutine().getId())
            .techCategory(entity.getTechCategory())
            .studyContent(entity.getStudyContent())
            .difficultParts(entity.getDifficultParts())
            .nextStudyPlan(entity.getNextStudyPlan())
            .isDeleted(entity.getIsDeleted())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}