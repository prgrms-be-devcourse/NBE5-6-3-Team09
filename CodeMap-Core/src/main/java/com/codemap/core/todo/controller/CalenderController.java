package com.codemap.core.todo.controller;

import com.codemap.core.todo.service.TodoService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class CalenderController {

    private final TodoService todoService;

    /** ✅ 날짜별 Todo 제목 목록을 내려주는 API */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, List<String>>> getTodoTitlesGroupedByDate(
        @SessionAttribute(name = "userId", required = false) Long userId) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, List<String>> result = todoService.getTodoTitlesGroupedByDate(userId);
        return ResponseEntity.ok(result);
    }
}