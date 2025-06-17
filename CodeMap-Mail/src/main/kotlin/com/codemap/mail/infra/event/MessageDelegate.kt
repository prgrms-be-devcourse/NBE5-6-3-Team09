package com.codemap.mail.infra.event

import com.codemap.mail.app.model.code.MailTemplatePath
import com.codemap.mail.infra.mail.MailTemplate
import com.codemap.mail.infra.mail.SmtpDto
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

interface MessageDelegate {
    fun handleMessage(message: String)
}

@Component
class EventMessageDelegate(
    private val mailTemplate: MailTemplate,
    private val objectMapper: ObjectMapper
) : MessageDelegate {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun handleMessage(message: String) = runBlocking {
        log.info("📨 메시지 수신: {}", message)

        try {
            val outbox = objectMapper.readValue(message, OutboxDto::class.java)

            when (outbox.eventType.uppercase()) {
                MailTemplatePath.SIGNUP_COMPLETE.name -> {
                    sendSignupCompleteMail(outbox)
                    log.info("✅ 회원가입 완료 메일 발송 완료: {}", outbox.payload)
                }
                MailTemplatePath.PASSWORD_RESET.name -> {
                    sendPasswordResetMail(outbox)
                    log.info("✅ 비밀번호 재설정 메일 발송 완료: {}", outbox.payload)
                }
                else -> {
                    log.warn("⚠️ 알 수 없는 이벤트 타입: {}", outbox.eventType)
                }
            }
        } catch (e: Exception) {
            log.error("❌ 메일 발송 처리 실패: {}", e.message, e)
        }
    }

    private suspend fun sendSignupCompleteMail(outbox: OutboxDto) {
        val dto = SmtpDto(
            from = "CodeMap <noreply@codemap.com>",
            to = outbox.payload,
            subject = "🎉 CodeMap에 오신 것을 환영합니다!",
            templatePath = MailTemplatePath.SIGNUP_COMPLETE.path,
            properties = mapOf(
                "email" to outbox.payload,
                "signupDate" to outbox.createdAt.toString()
            )
        )
        mailTemplate.send(dto)
    }

    private suspend fun sendPasswordResetMail(outbox: OutboxDto) {
        val dto = SmtpDto(
            from = "CodeMap <noreply@codemap.com>",
            to = outbox.payload,
            subject = "🔒 CodeMap 비밀번호 재설정 안내",
            templatePath = MailTemplatePath.PASSWORD_RESET.path
        )
        mailTemplate.send(dto)
    }
}