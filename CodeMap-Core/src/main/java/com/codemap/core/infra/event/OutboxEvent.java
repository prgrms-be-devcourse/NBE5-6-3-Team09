package com.codemap.core.infra.event;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    private String eventType;

    @Builder.Default
    private String sourceService = "core-service";

    private String payload;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Builder.Default
    private Boolean activated = true;
}