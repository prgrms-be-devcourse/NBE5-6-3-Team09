package com.codemap.core.mypage.controller;

import com.codemap.core.mypage.dto.RoutineCategoryCompletionDto;
import com.codemap.core.mypage.dto.RoutineCompletionDto;
import com.codemap.core.mypage.dto.UserStatDto;
import com.codemap.core.mypage.service.MyPageService;
import com.codemap.core.mypage.service.UserStatService;
import com.codemap.core.todo.domain.Todo;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.dto.NicknameUpdateDto;
import com.codemap.core.user.dto.PasswordUpdateDto;
import com.codemap.core.user.repository.UserRepository;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MyPageViewController {

    private final MyPageService myPageService;
    private final UserStatService userStatService;
    private final UserRepository userRepository;

    private static final List<String> WEEKDAYS = Arrays.asList("월", "화", "수", "목", "금", "토", "일");

    @GetMapping("/mypage/stats")
    public String showMyStats(Principal principal, Model model) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 기존 전체 통계 (차트용, 카테고리용)
        UserStatDto statDto = userStatService.getStatForUser(user.getId());
        RoutineCompletionDto completionDto = myPageService.getRoutineCompletionStats(user.getId()); // 전체
        List<RoutineCategoryCompletionDto> categoryStats = myPageService.getRoutineCompletionStatsByCategory(user.getId());
        Integer totalActualFocusTime = myPageService.getTotalActualFocusTime(user.getId());
        Map<String, Integer> actualFocusTimeByCategory = myPageService.getActualFocusTimeByCategory(user.getId());

        // 요일별 차트 데이터
        Map<String, Integer> rawFocusMap = myPageService.getFocusTimePerDay(user.getId());
        Map<String, Integer> orderedFocusMap = new LinkedHashMap<>();
        for (String day : WEEKDAYS) {
            orderedFocusMap.put(day, rawFocusMap.getOrDefault(day, 0));
        }

        // 오늘의 투두 (기존)
        List<Todo> todayTodos = myPageService.getTodayTodos(user.getId());

        // ✅ NEW: 오늘의 학습 요약용 데이터
        RoutineCompletionDto todayCompletion = myPageService.getTodayRoutineCompletionStats(user.getId());
        Integer todayActualFocusTime = myPageService.getTodayActualFocusTime(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("userStat", statDto);
        model.addAttribute("completion", completionDto);  // 전체 완료율 (차트용)
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("focusTimes", orderedFocusMap);
        model.addAttribute("todayTodos", todayTodos);
        model.addAttribute("totalActualFocusTime", totalActualFocusTime);
        model.addAttribute("actualFocusTimeByCategory", actualFocusTimeByCategory);

        // ✅ NEW: 오늘의 학습 요약용
        model.addAttribute("todayCompletion", todayCompletion);
        model.addAttribute("todayActualFocusTime", todayActualFocusTime);

        return "mypage/stats";
    }


    @GetMapping("/settings")
    public String settingsPage(Principal principal, Model model) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        model.addAttribute("user", user);
        model.addAttribute("nicknameDto", new NicknameUpdateDto());
        model.addAttribute("passwordDto", new PasswordUpdateDto());

        return "mypage/settings";
    }
}