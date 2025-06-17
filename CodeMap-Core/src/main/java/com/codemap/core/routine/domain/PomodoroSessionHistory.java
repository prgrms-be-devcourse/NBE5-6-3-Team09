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
@Table(name = "pomodoro_sessions_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PomodoroSessionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "original_session_id")
    private Long originalSessionId; // 원본 세션의 ID

    @Column(name = "original_routine_id")
    private Long originalRoutineId; // 원본 루틴의 ID

    @Column(name = "duration_minutes")
    private Integer durationMinutes; // 실제 진행된 시간(분)

    @Column(name = "started_at")
    private LocalDateTime startedAt; // 세션 시작 시간

    @Column(name = "ended_at")
    private LocalDateTime endedAt; // 세션 종료 시간

    @Column(name = "routine_date")
    private LocalDate routineDate; // 해당 루틴이 수행된 날짜

    @Column(name = "archived_at")
    private LocalDateTime archivedAt; // 히스토리로 옮겨진 시간

    @Column(updatable = false)
    private LocalDateTime createdAt; // 원본 세션의 생성 시간

    @PrePersist
    protected void onCreate() {
        this.archivedAt = LocalDateTime.now();
    }

    // PomodoroSession을 PomodoroSessionHistory로 변환하는 정적 메서드
    public static PomodoroSessionHistory fromPomodoroSession(PomodoroSession session, User user, LocalDate routineDate) {
        return PomodoroSessionHistory.builder()
            .user(user)
            .originalSessionId(session.getId())
            .originalRoutineId(session.getRoutine().getId())
            .durationMinutes(session.getDurationMinutes())
            .startedAt(session.getStartedAt())
            .endedAt(session.getEndedAt())
            .routineDate(routineDate)
            .createdAt(session.getStartedAt()) // 원본 세션의 시작시간을 생성시간으로 사용
            .build();
    }

    // 실제 진행 시간을 분 단위로 계산하는 메서드
    public Integer getActualDurationMinutes() {
        if (startedAt != null && endedAt != null) {
            long seconds = java.time.Duration.between(startedAt, endedAt).getSeconds();
            return seconds < 60 ? 0 : (int) Math.ceil(seconds / 60.0);
        }
        return durationMinutes != null ? durationMinutes : 0;
    }

    // 세션이 완료되었는지 확인하는 메서드
    public boolean isCompleted() {
        return endedAt != null;
    }
}