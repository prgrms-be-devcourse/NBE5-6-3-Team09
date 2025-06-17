package com.codemap.core.routine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pomodoro_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PomodoroSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private DailyRoutine routine;

    @Builder.Default
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer durationMinutes = 0;


    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    public Integer getActualDurationMinutes() {
        if (startedAt == null) return 0;

        LocalDateTime endTime = endedAt != null ? endedAt : LocalDateTime.now();
        long seconds = Duration.between(startedAt, endTime).getSeconds();

        // 1분 미만이면 0, 1분 이상이면 분 단위로 변환 (올림)
        return seconds < 60 ? 0 : (int) Math.ceil(seconds / 60.0);
    }
}