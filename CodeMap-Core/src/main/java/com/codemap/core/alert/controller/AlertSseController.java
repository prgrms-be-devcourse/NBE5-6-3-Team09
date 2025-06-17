package com.codemap.core.alert.controller;

import com.codemap.core.alert.service.AlertService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class AlertSseController {

    private final AlertService alertService;

    @GetMapping("/sse/alerts")
    public SseEmitter connect(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return alertService.subscribe(userId);
    }
}