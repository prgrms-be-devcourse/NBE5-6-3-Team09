package com.codemap.core.mypage.service;

import com.codemap.core.mypage.dto.RoutineCategoryCompletionDto;
import com.codemap.core.mypage.dto.RoutineCompletionDto;
import com.codemap.core.routine.domain.DailyRoutine;
import com.codemap.core.routine.repository.DailyRoutineRepository;
import com.codemap.core.todo.domain.Todo;
import com.codemap.core.todo.repository.TodoRepository;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final DailyRoutineRepository dailyRoutineRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    private static final Map<Integer, String> WEEKDAY_MAP = Map.of(
        2, "월", 3, "화", 4, "수", 5, "목", 6, "금", 7, "토", 1, "일"
    );

    public RoutineCompletionDto getRoutineCompletionStats(Long userId) {
        long total = dailyRoutineRepository.countAllByUserId(userId);
        long completed = dailyRoutineRepository.countCompletedByUserId(userId);
        return new RoutineCompletionDto(completed, total);
    }

    public List<RoutineCategoryCompletionDto> getRoutineCompletionStatsByCategory(Long userId) {
        List<Object[]> results = dailyRoutineRepository.countByCategory(userId);
        return results.stream()
            .map(row -> new RoutineCategoryCompletionDto(
                (String) row[0],
                ((Number) row[1]).longValue(),
                ((Number) row[2]).longValue()
            ))
            .toList();
    }

    public Map<String, Integer> getFocusTimePerDay(Long userId) {
        List<Object[]> result = dailyRoutineRepository.sumActualFocusTimeGroupedByWeekday(userId);
        Map<String, Integer> focusMap = new LinkedHashMap<>();

        // 요일 순서대로 초기화
        List<String> order = List.of("월", "화", "수", "목", "금", "토", "일");
        for (String day : order) {
            focusMap.put(day, 0);
        }

        for (Object[] row : result) {
            Integer weekdayNumber = ((Number) row[0]).intValue(); // 1~7
            Integer focusTime = row[1] != null ? ((Number) row[1]).intValue() : 0;
            String weekday = WEEKDAY_MAP.get(weekdayNumber);
            focusMap.put(weekday, focusTime);
        }

        return focusMap;
    }
    public Integer getTotalActualFocusTime(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Integer total = dailyRoutineRepository.getTotalActualFocusTimeByUser(user);
        return total != null ? total : 0;
    }

    public Map<String, Integer> getActualFocusTimeByCategory(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Object[]> results = dailyRoutineRepository.getTotalActualFocusTimeByCategory(user);
        Map<String, Integer> categoryMap = new HashMap<>();

        for (Object[] row : results) {
            String category = (String) row[0];
            Integer actualTime = row[1] != null ? ((Number) row[1]).intValue() : 0;
            categoryMap.put(category, actualTime);
        }

        return categoryMap;
    }

    public List<Todo> getTodayTodos(Long userId) {
        return todoRepository.findTodayTodos(LocalDate.now(), userId);
    }

    // ✅ NEW: 오늘의 루틴 완료 통계
    public RoutineCompletionDto getTodayRoutineCompletionStats(Long userId) {
        LocalDate today = LocalDate.now();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 오늘 생성된 루틴들 조회 (DailyRoutineRepositoryCustom 활용)
        List<DailyRoutine> todayRoutines = dailyRoutineRepository.findRoutinesByUserAndDate(user, today);

        long totalCount = todayRoutines.size();
        long completedCount = todayRoutines.stream()
            .filter(routine -> "COMPLETED".equals(routine.getStatus()))
            .count();

        return new RoutineCompletionDto(completedCount, totalCount);
    }

    // ✅ NEW: 오늘의 실제 집중 시간
    public Integer getTodayActualFocusTime(Long userId) {
        LocalDate today = LocalDate.now();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 오늘 생성되고 완료된 루틴들의 실제 집중 시간 합계
        List<DailyRoutine> todayRoutines = dailyRoutineRepository.findRoutinesByUserAndDate(user, today);

        return todayRoutines.stream()
            .filter(routine -> "COMPLETED".equals(routine.getStatus()))
            .mapToInt(routine -> routine.getActualFocusTime() != null ? routine.getActualFocusTime() : 0)
            .sum();
    }
}