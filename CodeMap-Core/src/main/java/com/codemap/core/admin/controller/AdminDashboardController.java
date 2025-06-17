package com.codemap.core.admin.controller;

import com.codemap.core.admin.service.AdminUserService;
import com.codemap.core.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final AdminUserService adminUserService;
    private final InterviewService interviewService;

    /**
     * 관리자 대시보드 메인 페이지
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        log.info("관리자 대시보드 접근");

        try {
            // 📊 기본 통계 데이터 수집

            // 1. 총 사용자 수 (ROLE_USER만)
            int totalUsers = adminUserService.findAllUsers().size();

            // 2. 활성 사용자 수 (현재는 전체 사용자와 동일, 나중에 로그인 기준으로 변경 가능)
            int activeUsers = totalUsers;

            // 3. 총 면접 질문 수
            int totalQuestions = interviewService.findAll().size();

            // 4. 시스템 알림 (현재는 하드코딩, 나중에 실제 알림 시스템 구현)
            int systemAlerts = 0;

            // 📈 모델에 데이터 추가
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("activeUsers", activeUsers);
            model.addAttribute("totalQuestions", totalQuestions);
            model.addAttribute("systemAlerts", systemAlerts);

            log.info("대시보드 데이터 로드 완료 - 사용자: {}, 질문: {}", totalUsers, totalQuestions);

        } catch (Exception e) {
            log.error("대시보드 데이터 로드 중 오류 발생", e);

            // 오류 발생 시 기본값 설정
            model.addAttribute("totalUsers", 0);
            model.addAttribute("activeUsers", 0);
            model.addAttribute("totalQuestions", 0);
            model.addAttribute("systemAlerts", 1); // 오류 표시
        }

        return "admin/admin-main"; // templates/admin/admin-main.html
    }

    /**
     * 관리자 메인 리다이렉트 (기본 /admin 접근 시)
     */
    @GetMapping("")
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    /**
     * 관리자 인덱스 리다이렉트 (혹시 모를 다른 경로)
     */
    @GetMapping("/")
    public String redirectToDashboardSlash() {
        return "redirect:/admin/dashboard";
    }
}