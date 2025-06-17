package com.codemap.core.infra.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ErrorPageController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        Integer statusCode = null;
        if (status != null) {
            statusCode = Integer.valueOf(status.toString());
        }

        // 로깅
        log.error("Error occurred - Status: {}, Message: {}", statusCode, message);
        if (exception != null) {
            log.error("Exception: ", (Throwable) exception);
        }

        // 모델에 에러 정보 추가
        model.addAttribute("status", statusCode);
        model.addAttribute("message", getErrorMessage(statusCode, message));

        // 개발 환경에서만 스택 트레이스 표시
        if (exception != null && isDevelopmentMode()) {
            model.addAttribute("trace", getStackTrace((Throwable) exception));
        }

        return "error/error";
    }

    private String getErrorMessage(Integer statusCode, Object message) {
        if (statusCode != null) {
            return switch (statusCode) {
                case 404 -> "요청하신 페이지를 찾을 수 없습니다.";
                case 403 -> "해당 페이지에 접근할 권한이 없습니다.";
                case 401 -> "로그인이 필요한 페이지입니다.";
                case 500 -> "서버 내부 오류가 발생했습니다.";
                case 503 -> "서비스를 일시적으로 사용할 수 없습니다.";
                default -> message != null ? message.toString() : "알 수 없는 오류가 발생했습니다.";
            };
        }
        return message != null ? message.toString() : "알 수 없는 오류가 발생했습니다.";
    }

    private String getStackTrace(Throwable throwable) {
        if (throwable == null) return null;

        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getSimpleName())
            .append(": ")
            .append(throwable.getMessage())
            .append("\n\n");

        for (StackTraceElement element : throwable.getStackTrace()) {
            if (element.getClassName().startsWith("com.grepp.codemap")) {
                sb.append("at ").append(element.toString()).append("\n");
            }
        }

        return sb.toString();
    }

    private boolean isDevelopmentMode() {
        // 개발 환경 판단 로직 (프로파일이나 환경변수로 판단)
        String profile = System.getProperty("spring.profiles.active", "");
        return profile.contains("dev") || profile.isEmpty(); // 기본값은 개발모드
    }
}