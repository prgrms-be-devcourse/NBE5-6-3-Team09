package com.codemap.core.user.controller;

import com.codemap.core.infra.auth.Role;
import com.codemap.core.infra.error.exceptions.CommonException;
import com.codemap.core.mypage.service.MyPageService;
import com.codemap.core.routine.dto.DailyRoutineDto;
import com.codemap.core.routine.service.DailyRoutineService;
import com.codemap.core.todo.dto.TodoResponse;
import com.codemap.core.todo.service.TodoService;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.form.SigninForm;
import com.codemap.core.user.form.SignupForm;
import com.codemap.core.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {


    private final UserService userService;
    private final DailyRoutineService dailyRoutineService;
    private final TodoService todoService;
    private final MyPageService myPageService;

    @GetMapping("signin")
    public String signin(SigninForm form, Model model) {
        return "user/signin";
    }

    @GetMapping("/main")
    public String main(HttpSession session, Model model) {
        // 세션 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/signin";
        }

        try {
            // 사용자 정보 조회
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);

            // 오늘 날짜 기준으로 루틴 조회
            LocalDate today = LocalDate.now();
            Map<String, List<DailyRoutineDto>> routinesByStatus =
                dailyRoutineService.getRoutinesByDate(userId, today);

            // 오늘의 투두 조회
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
            List<TodoResponse> todayTodos = todoService.getTodosByDate(userId, startOfDay, endOfDay);

            // 통계 데이터 계산
            List<DailyRoutineDto> completedRoutines = routinesByStatus.get("completed");
            List<DailyRoutineDto> activeRoutines = routinesByStatus.get("active");
            List<DailyRoutineDto> passedRoutines = routinesByStatus.get("passed");

            // 오늘의 총 집중시간 계산 (완료된 루틴들의 집중시간 합)
            int totalActualFocusMinutes = completedRoutines.stream()
                .mapToInt(routine -> routine.getActualFocusTime() != null ? routine.getActualFocusTime() : 0)
                .sum();

            // 전체 루틴 수와 완료된 루틴 수
            int totalRoutines = activeRoutines.size() + completedRoutines.size() + passedRoutines.size();
            int completedRoutineCount = completedRoutines.size();

            // 완료율 계산
            double completionRate = totalRoutines > 0 ? (double) completedRoutineCount / totalRoutines * 100 : 0;

            // ✅ NEW: MyPageService를 통해 추가 통계 데이터 가져오기
            // 총 학습시간 (분 단위)
            Integer totalActualFocusTime = myPageService.getTotalActualFocusTime(userId);

            // 카테고리별 학습시간
            Map<String, Integer> actualFocusTimeByCategory = myPageService.getActualFocusTimeByCategory(userId);

            // 가장 많이 한 카테고리 찾기
            String topCategory = null;
            Integer topCategoryTime = null;

            if (!actualFocusTimeByCategory.isEmpty()) {
                Map.Entry<String, Integer> topEntry = actualFocusTimeByCategory.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);

                if (topEntry != null && topEntry.getValue() > 0) {
                    topCategory = topEntry.getKey();
                    topCategoryTime = topEntry.getValue();
                }
            }

            // 모델에 데이터 추가
            model.addAttribute("activeRoutines", activeRoutines);
            model.addAttribute("completedRoutines", completedRoutines);
            model.addAttribute("passedRoutines", passedRoutines);
            model.addAttribute("todayTodos", todayTodos);
            model.addAttribute("totalActualFocusMinutes", totalActualFocusMinutes);
            model.addAttribute("totalRoutines", totalRoutines);
            model.addAttribute("completedRoutineCount", completedRoutineCount);
            model.addAttribute("completionRate", Math.round(completionRate));
            model.addAttribute("today", today);

            // ✅ NEW: 추가 통계 데이터
            model.addAttribute("totalActualFocusTime", totalActualFocusTime);
            model.addAttribute("topCategory", topCategory);
            model.addAttribute("topCategoryTime", topCategoryTime);

        } catch (Exception e) {
            log.error("메인 페이지 로딩 중 오류 발생", e);
            return "redirect:/user/signin";
        }

        return "user/main";
    }

    @PostMapping("signin")
    public String signin(
        @Valid SigninForm form,
        BindingResult bindingResult,
        HttpSession session,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "user/signin";
        }

        try {
            // 로그인 성공 시
            User user = userService.login(form);

            // ✅ 세션 저장
            session.setAttribute("userId", user.getId());

            // 역할에 따른 SimpleGrantedAuthority 생성 - 조건부 접두사 추가
            SimpleGrantedAuthority authority;

            if (user.getRole().startsWith("ROLE_")) {
                authority = new SimpleGrantedAuthority(user.getRole());
            } else {
                authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
            }

            // ✅ SecurityContext 수동 인증 설정
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    List.of(authority) // 예: ROLE_USER
                );
            // ✅ 핵심: SecurityContext를 세션에 직접 저장해야 유지됨
            SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
            );

            // 사용자 역할 체크 및 리다이렉트
            if (user.getRole().equals("ROLE_ADMIN") ||
                user.getRole().equals(Role.ROLE_ADMIN.name())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/user/main";
        } catch (CommonException e) {
            model.addAttribute("loginError", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return "user/signin";
        }
    }


    @GetMapping("signup")
    public String signup(SignupForm form){
        return "user/signup";
    }

    @PostMapping("signup")
    public String signup(
        @Valid SignupForm form,
        BindingResult bindingResult,
        Model model){


        if(bindingResult.hasErrors()){
            return "user/signup";
        }

        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "error.passwordConfirm", "비밀번호가 일치하지 않습니다.");
            return "user/signup";
        }

        log.info("회원가입 요청: email={}, password={}, passwordConfirm={}",
            form.getEmail(), form.getPassword(), form.getPasswordConfirm());

        userService.signup(form.toDto(), Role.ROLE_USER);
        return "redirect:/";
    }


    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 삭제
        }

        SecurityContextHolder.clearContext(); // Security 인증 정보 제거
        return "redirect:/user/signin";
    }
}