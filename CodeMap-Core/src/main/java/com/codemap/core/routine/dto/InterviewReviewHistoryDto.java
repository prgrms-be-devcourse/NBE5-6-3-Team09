package com.codemap.core.routine.dto;

import com.codemap.core.routine.domain.InterviewReviewHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewReviewHistoryDto {
    private Long id;
    private Long userId;
    private Long originalReviewId;
    private Long originalRoutineId;
    private String techCategory;
    private String studyContent;
    private String learnedConcepts;
    private String difficultParts;
    private String nextStudyPlan;
    private LocalDate routineDate;
    private LocalDateTime archivedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InterviewReviewHistoryDto fromEntity(InterviewReviewHistory entity) {
        return InterviewReviewHistoryDto.builder()
            .id(entity.getId())
            .userId(entity.getUser().getId())
            .originalReviewId(entity.getOriginalReviewId())
            .originalRoutineId(entity.getOriginalRoutineId())
            .techCategory(entity.getTechCategory())
            .studyContent(entity.getStudyContent())
            .learnedConcepts(entity.getLearnedConcepts())
            .difficultParts(entity.getDifficultParts())
            .nextStudyPlan(entity.getNextStudyPlan())
            .routineDate(entity.getRoutineDate())
            .archivedAt(entity.getArchivedAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}