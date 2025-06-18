package com.codemap.core.todo.controller;

import com.codemap.core.todo.domain.Todo;
import com.codemap.core.todo.dto.TodoCreateRequest;
import com.codemap.core.todo.dto.TodoResponse;
import com.codemap.core.todo.dto.TodoUpdateRequest;
import com.codemap.core.todo.service.TodoService;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    /** ✅ 1. 캘린더 진입 화면 */
    @GetMapping("/calender")
    public String showCalendarPage(Model model,
        @SessionAttribute(name = "userId", required = false) Long userId) {

        User user = userService.getUserById(userId);

        model.addAttribute("user", user);

        return "todo/calender";
    }

    /** ✅ 2. 선택 날짜 투두 조회 */
    @GetMapping
    public String getTodoList(@RequestParam("date") String date,
        HttpSession session,
        Model model,
        @SessionAttribute(name = "userId", required = false) Long userId) {

        User user = userService.getUserById(userId);

        model.addAttribute("user", user);

        userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            model.addAttribute("todos", List.of());  // 빈 리스트
            model.addAttribute("selectedDate", date);
            return "todo/todo-list";
        }

        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

        List<TodoResponse> todos = todoService.getTodosByDate(userId, startOfDay, endOfDay);
        model.addAttribute("todos", todos);
        model.addAttribute("selectedDate", date);
        return "todo/todo-list";
    }

    /** ✅ 3. 추가 폼 (모달에서 불러옴) */
    @GetMapping("/new")
    public String showCreateForm(@RequestParam("date") String date, Model model) {
        model.addAttribute("selectedDate", date);
        return "todo/todo-form";
    }

    /** ✅ 4. 투두 생성 */
    @PostMapping
    public String createTodo(HttpSession session,
        @RequestParam("selectedDate") String selectedDate,
        @ModelAttribute @Valid TodoCreateRequest request) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("로그인된 사용자만 투두를 추가할 수 있습니다.");
        }

        LocalDate parsedStartDate = LocalDate.parse(selectedDate);                 // ✅ 시작 날짜
        LocalTime parsedStartTime = LocalTime.parse(request.startTime());         // ✅ 시작 시간
        LocalDate parsedCompletedDate = LocalDate.parse(request.completedAt());   // ✅ 마감 날짜

        LocalDateTime startTime = LocalDateTime.of(parsedStartDate, parsedStartTime);
        LocalDateTime completedAt = parsedCompletedDate.atTime(LocalTime.MAX);

        Todo created = todoService.createTodo(
            userId,
            request.title(),
            request.description(),
            startTime,
            completedAt
        );

        return "redirect:/todos?date=" + startTime.toLocalDate(); // ✅ 시작일 기준으로 이동
    }

    // ✅ 5. 수정 폼 (모달에서 불러옴)
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
        HttpSession session,
        @RequestParam("date") String date,
        Model model) {
        Long userId = (Long) session.getAttribute("userId");
        Todo todo = todoService.findByIdAndUser(id, userId);

        model.addAttribute("todo", todo);
        model.addAttribute("selectedDate", date);
        return "todo/todo-update";
    }

    // 메서드 추가
    /** ✅ 투두 수정을 위한 JSON 데이터 조회 API */
    @GetMapping("/{id}/edit/json")
    @ResponseBody
    public ResponseEntity<TodoResponse> getTodoForEdit(@PathVariable Long id,
        HttpSession session,
        @RequestParam("date") String date) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Todo todo = todoService.findByIdAndUser(id, userId);
            TodoResponse response = TodoResponse.of(todo);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ 6. 투두 수정
    @PatchMapping("/{id}")
    public String updateTodo(@PathVariable Long id,
        HttpSession session,
        @RequestParam("selectedDate") String selectedDate,
        @ModelAttribute @Valid TodoUpdateRequest request) {

        Long userId = (Long) session.getAttribute("userId");

        // ✅ 시작 날짜는 selectedDate 기준
        LocalDate parsedStartDate = LocalDate.parse(selectedDate);
        LocalTime parsedStartTime = LocalTime.parse(request.startTime());
        LocalDate parsedCompletedDate = LocalDate.parse(request.completedAt());

        LocalDateTime startTime = LocalDateTime.of(parsedStartDate, parsedStartTime);
        LocalDateTime completedAt = parsedCompletedDate.atTime(LocalTime.MAX);

        Todo updated = todoService.updateTodo(
            id,
            userId,
            request.title(),
            request.description(),
            startTime,
            completedAt
        );

        return "redirect:/todos?date=" + startTime.toLocalDate(); // ✅ 시작일 기준으로 유지
    }

    /** ✅ 7. 투두 삭제 - RESTful DELETE 방식 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id,
        HttpSession session,
        @RequestParam("date") String date) {
        System.out.println("🟡 삭제 컨트롤러 도착함");

        Long userId = (Long) session.getAttribute("userId");
        todoService.deleteTodo(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/confirm-delete")
    public String showDeleteModal(@PathVariable Long id,
        @RequestParam("date") String date,
        Model model) {
        model.addAttribute("id", id);
        model.addAttribute("selectedDate", date);
        return "todo/todo-delete"; // HTML 모달 템플릿 파일
    }

    /** ✅ 완료 상태 토글 (main.html AJAX 요청 대응용) */
    @PostMapping("/{id}/complete")
    @ResponseBody
    public ResponseEntity<Void> toggleComplete(@PathVariable Long id,
        HttpSession session,
        @RequestParam("date") String date) {
        Long userId = (Long) session.getAttribute("userId");
        todoService.toggleComplete(id, userId);
        return ResponseEntity.ok().build(); // 새로고침 없이 응답만 돌려줌
    }
}