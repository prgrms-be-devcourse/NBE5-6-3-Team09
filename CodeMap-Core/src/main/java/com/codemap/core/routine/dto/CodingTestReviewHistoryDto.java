package com.codemap.core.routine.dto;

import com.codemap.core.routine.domain.CodingTestReviewHistory;
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
public class CodingTestReviewHistoryDto {
    private Long id;
    private Long userId;
    private Long originalReviewId;
    private Long originalRoutineId;
    private String problemTitle;
    private String problemDescription;
    private String mySolution;
    private String correctSolution;
    private String problemType;
    private LocalDate routineDate;
    private LocalDateTime archivedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CodingTestReviewHistoryDto fromEntity(CodingTestReviewHistory entity) {
        return CodingTestReviewHistoryDto.builder()
            .id(entity.getId())
            .userId(entity.getUser().getId())
            .originalReviewId(entity.getOriginalReviewId())
            .originalRoutineId(entity.getOriginalRoutineId())
            .problemTitle(entity.getProblemTitle())
            .problemDescription(entity.getProblemDescription())
            .mySolution(entity.getMySolution())
            .correctSolution(entity.getCorrectSolution())
            .problemType(entity.getProblemType())
            .routineDate(entity.getRoutineDate())
            .archivedAt(entity.getArchivedAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}