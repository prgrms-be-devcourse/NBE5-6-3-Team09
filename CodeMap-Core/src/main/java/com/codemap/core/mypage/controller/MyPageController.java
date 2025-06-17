package com.codemap.core.mypage.controller;

import com.codemap.core.mypage.dto.RoutineCategoryCompletionDto;
import com.codemap.core.mypage.dto.RoutineCompletionDto;
import com.codemap.core.mypage.dto.UserStatDto;
import com.codemap.core.mypage.service.MyPageService;
import com.codemap.core.mypage.service.UserStatService;
import com.codemap.core.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final UserStatService userStatService;
    private final MyPageService myPageService;

    @GetMapping("/stats/{userId}")
    public UserStatDto getUserStats(@PathVariable Long userId) {
        return userStatService.getStatForUser(userId);
    }

    @GetMapping("completion-rate")
    public ResponseEntity<RoutineCompletionDto> getRoutineCompletionRate(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(myPageService.getRoutineCompletionStats(user.getId()));
    }

    @GetMapping("completion-rate/category")
    public ResponseEntity<List<RoutineCategoryCompletionDto>> getRoutineCompletionRateByCategory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(myPageService.getRoutineCompletionStatsByCategory(user.getId()));
    }
}