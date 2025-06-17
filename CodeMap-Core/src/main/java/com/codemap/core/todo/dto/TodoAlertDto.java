package com.codemap.core.todo.dto;

import java.time.LocalDateTime;

/**
 * SSE 알림용 DTO
 * - 10분 전 시작 예정인 투두 제목과 시작 시간을 클라이언트에 전달
 */
public record TodoAlertDto(
    String title,
    LocalDateTime startTime
) {

}