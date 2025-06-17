package com.codemap.core.todo.service;

import com.codemap.core.todo.domain.Todo;
import com.codemap.core.todo.dto.TodoAlertDto;
import com.codemap.core.todo.dto.TodoResponse;
import com.codemap.core.todo.repository.TodoRepository;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Todo 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    // 생성자를 통한 의존성 주입
    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }
    // 캘린더 렌더링 위해 사용자 전체 Todo 리스트 조회
    @Transactional(readOnly = true)
    public List<Todo> findAllByUser(Long userId) {
        return todoRepository.findAllByUser_Id(userId);
    }

    /**
     * 특정 기간 동안 사용자의 Todo 목록을 조회한다.
     *
     * @param userId 사용자 ID
     * @param start  조회 시작 시각 (inclusive)
     * @param end    조회 종료 시각 (inclusive)
     * @return 기간 내 Todo 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByDate(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Todo> todos = todoRepository.findAllByUser_IdAndStartTimeBetween(userId, start, end);
        return todos.stream()
            .sorted(Comparator.comparing(Todo::getIsCompleted)) // 완료된 투두는 맨 아래로 이동
            .map(TodoResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, List<String>> getTodoTitlesGroupedByDate(Long userId) {
        List<Todo> todos = todoRepository.findAllByUser_Id(userId);

        return todos.stream()
            .collect(Collectors.groupingBy(
                todo -> todo.getStartTime().toLocalDate().format(DateTimeFormatter.ISO_DATE),
                Collectors.mapping(Todo::getTitle, Collectors.toList())
            ));
    }

    @Transactional(readOnly = true)
    public Todo getTodosForDate(Long userId, LocalDateTime start, LocalDateTime end) {
        return (Todo) todoRepository.findAllByUser_IdAndStartTimeBetween(userId, start, end);
    }

    @Transactional(readOnly = true)
    public Todo findByIdAndUser(Long id, Long userId) {
        Todo todo = todoRepository.findByIdAndUser_Id(id, userId);
        if (todo == null) {
            throw new IllegalArgumentException("해당 Todo를 찾을 수 없습니다.");
        }
        return todo;
    }

    public Todo createTodo(Long userId,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime completedAt) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        Todo todo = Todo.builder()
            .user(user)
            .title(title)
            .description(description)
            .startTime(startTime)
            .completedAt(completedAt)
            .isCompleted(false)
            .isDeleted(false)
            .build();

        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long id,
        Long userId,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime completedAt) {
        Todo todo = findByIdAndUser(id, userId);
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setStartTime(startTime);
        todo.setCompletedAt(completedAt);
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id, Long userId) {
        System.out.println("🟢 서비스에서도 삭제 호출됨: " + id);

        Todo todo = findByIdAndUser(id, userId);
        todoRepository.delete(todo);
    }

    public Todo toggleComplete(Long id, Long userId) {
        Todo todo = findByIdAndUser(id, userId);
        boolean current = todo.getIsCompleted();
        todo.setIsCompleted(!current);
        return todoRepository.save(todo);
    }

    public List<Todo> getTodosToNotify(Long userId, LocalDateTime now, LocalDateTime tenMinutesLater) {
        return todoRepository.findAllByUser_IdAndStartTimeBetweenAndIsCompletedFalse(
            userId, now, tenMinutesLater);
    }

    public List<Long> findAllUserIds() {
        return todoRepository.findDistinctUserIds();
    }

    public List<TodoAlertDto> findTodosStartingIn10Minutes(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in10Min = now.plusMinutes(10);

        return todoRepository.findByUserIdAndStartTimeBetween(userId, now, in10Min)
            .stream()
            .filter(todo -> !todo.getIsCompleted())
            .map(todo -> new TodoAlertDto(todo.getTitle(), todo.getStartTime()))
            .toList();
    }
}