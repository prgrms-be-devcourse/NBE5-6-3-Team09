package com.codemap.core.infra.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String MAIL_CHANNEL = "core-service";

    /**
     * 메일 이벤트를 Redis Pub/Sub으로 발행
     */
    public void publishMailEvent(String eventType, String email) {
        log.info("🔍 [DEBUG] publishMailEvent 호출됨 - Type: {}, Email: {}", eventType, email);

        try {
            log.info("🔍 [DEBUG] RedisTemplate 상태: {}", redisTemplate != null ? "정상" : "NULL");
            log.info("🔍 [DEBUG] ObjectMapper 상태: {}", objectMapper != null ? "정상" : "NULL");

            OutboxEvent event = OutboxEvent.builder()
                .eventType(eventType)
                .payload(email)
                .build();

            log.info("🔍 [DEBUG] OutboxEvent 생성 완료: {}", event);

            log.info("🔍 [DEBUG] Redis 채널로 발송 시작 - 채널: {}", MAIL_CHANNEL);
            redisTemplate.convertAndSend(MAIL_CHANNEL, event);
            log.info("📧 메일 이벤트 발행 성공 - Type: {}, Email: {}", eventType, email);

        }catch (Exception e) {
            log.error("❌ 메일 이벤트 발행 실패 - Type: {}, Email: {}, 오류 타입: {}",
                eventType, email, e.getClass().getSimpleName(), e);
        }
    }

    /**
     * 회원가입 완료 메일 이벤트 발행
     */
    public void publishSignupCompleteEvent(String email) {
        log.info("🔍 [DEBUG] publishSignupCompleteEvent 호출됨 - Email: {}", email);
        publishMailEvent("SIGNUP_COMPLETE", email);
    }

    /**
     * 비밀번호 재설정 메일 이벤트 발행 (향후 확장용)
     */
    public void publishPasswordResetEvent(String email) {
        log.info("🔍 [DEBUG] publishPasswordResetEvent 호출됨 - Email: {}", email);
        publishMailEvent("PASSWORD_RESET", email);
    }
}