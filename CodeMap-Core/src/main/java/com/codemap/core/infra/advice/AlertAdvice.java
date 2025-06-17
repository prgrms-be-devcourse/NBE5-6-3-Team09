package com.codemap.core.infra.advice;

import com.codemap.core.todo.dto.TodoResponse;
import com.codemap.core.todo.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class AlertAdvice {

    private final TodoService todoService;

    @ModelAttribute("alertTodos")
    public List<TodoResponse> addAlertTodos(HttpServletRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Collections.emptyList();

        String dateParam = request.getParameter("date");
        LocalDateTime now;

        if (dateParam != null) {
            LocalDate date = LocalDate.parse(dateParam);
            now = date.atTime(LocalTime.now());
        } else {
            now = LocalDateTime.now(); // date 파라미터 없으면 현재 시간 기준
        }

        LocalDateTime after10 = now.plusMinutes(10);

        return todoService.getTodosToNotify(userId, now, after10)
            .stream()
            .map(TodoResponse::of)
            .toList();
    }

}
