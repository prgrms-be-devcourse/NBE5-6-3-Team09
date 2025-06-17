package com.codemap.core.routine.dto;

import com.codemap.core.routine.domain.DailyRoutine;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyRoutineDto {
    private Long id;
    private Long userId;
    private String category;
    private String title;
    private String description;
    private String status;
    private Integer focusTime;
    private Integer actualFocusTime;
    private Integer breakTime;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DailyRoutineDto fromEntity(DailyRoutine entity) {
        return DailyRoutineDto.builder()
            .id(entity.getId())
            .userId(entity.getUser().getId())
            .category(entity.getCategory())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .status(entity.getStatus())
            .focusTime(entity.getFocusTime())
            .actualFocusTime(entity.getActualFocusTime())
            .breakTime(entity.getBreakTime())
            .startedAt(entity.getStartedAt())
            .completedAt(entity.getCompletedAt())
            .isDeleted(entity.getIsDeleted())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}