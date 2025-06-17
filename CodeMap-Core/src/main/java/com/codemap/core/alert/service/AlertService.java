package com.codemap.core.alert.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
public class AlertService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60); // 1시간 타임아웃
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event().name("CONNECTED").data("connected"));
        } catch (IOException e) {
            log.error("[SSE] 연결 실패", e);
        }

        return emitter;
    }

    public void sendAlert(Long userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("ALERT").data(data));
            } catch (IOException e) {
                emitters.remove(userId);
                log.error("[SSE] 알림 전송 실패", e);
            }
        }
    }
}