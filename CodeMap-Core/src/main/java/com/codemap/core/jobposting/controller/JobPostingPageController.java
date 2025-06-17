package com.codemap.core.jobposting.controller;

import com.codemap.core.jobposting.service.JobPostingService;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class JobPostingPageController {

    private final JobPostingService jobPostingService;
    private final UserService userService;

    /**
     * 채용 공고 안내 페이지 출력 (목표기업 리스트 포함)
     */
    @GetMapping("/jobposting/page")
    public String jobpostingPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
        }
        return "jobposting/jobposting-list";
    }
}