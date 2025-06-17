package com.codemap.core.alert.scheduler;

import com.codemap.core.alert.service.AlertService;
import com.codemap.core.todo.dto.TodoAlertDto;
import com.codemap.core.todo.service.TodoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertScheduler {

    private final TodoService todoService;
    private final AlertService alertService;

    @Scheduled(fixedRate = 30000) // 매 1분마다 실행
    public void checkTodosStartingSoon() {
        List<Long> userIds = todoService.findAllUserIds(); // 이 메서드는 새로 만들어줘야 해!

        for (Long userId : userIds) {
            List<TodoAlertDto> todos = todoService.findTodosStartingIn10Minutes(userId);
            if (!todos.isEmpty()) {
                alertService.sendAlert(userId, todos);
            }
        }
    }
}