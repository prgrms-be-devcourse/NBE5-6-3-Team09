package com.codemap.core.routine.dto;

import com.codemap.core.routine.domain.PomodoroSessionHistory;
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
public class PomodoroSessionHistoryDto {
    private Long id;
    private Long userId;
    private Long originalSessionId;
    private Long originalRoutineId;
    private Integer durationMinutes;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDate routineDate;
    private LocalDateTime archivedAt;
    private LocalDateTime createdAt;

    // 실제 진행 시간 계산
    private Integer actualDurationMinutes;

    // 완료 여부
    private boolean completed;

    public static PomodoroSessionHistoryDto fromEntity(PomodoroSessionHistory entity) {
        return PomodoroSessionHistoryDto.builder()
            .id(entity.getId())
            .userId(entity.getUser().getId())
            .originalSessionId(entity.getOriginalSessionId())
            .originalRoutineId(entity.getOriginalRoutineId())
            .durationMinutes(entity.getDurationMinutes())
            .startedAt(entity.getStartedAt())
            .endedAt(entity.getEndedAt())
            .routineDate(entity.getRoutineDate())
            .archivedAt(entity.getArchivedAt())
            .createdAt(entity.getCreatedAt())
            .actualDurationMinutes(entity.getActualDurationMinutes())
            .completed(entity.isCompleted())
            .build();
    }

    // 세션 지속 시간을 문자열로 반환 (예: "25분", "1시간 30분")
    public String getFormattedDuration() {
        if (actualDurationMinutes == null || actualDurationMinutes == 0) {
            return "0분";
        }

        int hours = actualDurationMinutes / 60;
        int minutes = actualDurationMinutes % 60;

        if (hours > 0) {
            return minutes > 0 ?
                String.format("%d시간 %d분", hours, minutes) :
                String.format("%d시간", hours);
        } else {
            return String.format("%d분", minutes);
        }
    }

    // 세션 상태를 문자열로 반환
    public String getStatusText() {
        return completed ? "완료" : "미완료";
    }
}