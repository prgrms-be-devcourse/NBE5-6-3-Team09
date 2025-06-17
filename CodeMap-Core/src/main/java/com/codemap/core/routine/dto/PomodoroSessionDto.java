package com.codemap.core.routine.dto;

import com.codemap.core.routine.domain.PomodoroSession;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PomodoroSessionDto {
    private Long id;
    private Long routineId;
    private Integer durationMinutes;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public static PomodoroSessionDto fromEntity(PomodoroSession entity) {
        return PomodoroSessionDto.builder()
            .id(entity.getId())
            .routineId(entity.getRoutine().getId())
            .durationMinutes(entity.getDurationMinutes())
            .startedAt(entity.getStartedAt())
            .endedAt(entity.getEndedAt())
            .build();
    }
}