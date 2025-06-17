package com.codemap.core.routine.controller;

import com.codemap.core.routine.domain.PomodoroSession;
import com.codemap.core.routine.dto.CodingTestReviewDto;
import com.codemap.core.routine.dto.CodingTestReviewHistoryDto;
import com.codemap.core.routine.dto.DailyRoutineDto;
import com.codemap.core.routine.dto.InterviewReviewDto;
import com.codemap.core.routine.dto.InterviewReviewHistoryDto;
import com.codemap.core.routine.dto.PomodoroSessionDto;
import com.codemap.core.routine.dto.PomodoroSessionHistoryDto;
import com.codemap.core.routine.dto.RoutineHistoryDto;
import com.codemap.core.routine.repository.PomodoroSessionRepository;
import com.codemap.core.routine.scheduler.RoutineScheduler;
import com.codemap.core.routine.service.DailyRoutineService;
import com.codemap.core.routine.service.RoutineHistoryService;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/routines")
@RequiredArgsConstructor
@Slf4j
public class RoutineController {

    private final DailyRoutineService dailyRoutineService;
    private final RoutineHistoryService routineHistoryService;
    private final RoutineScheduler routineScheduler;
    private final UserService userService;
    private final PomodoroSessionRepository pomodoroSessionRepository;

    // 루틴 목록 조회 (날짜별)

    @GetMapping
    public String getRoutines(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        HttpSession session,
        Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin"; // 로그인 페이지로 리다이렉트
        }

        try {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
        } catch (Exception e) {
            log.error("사용자 정보 조회 실패: ", e);
            return "redirect:/signin";
        }

        LocalDate selectedDate = date != null ? date : LocalDate.now();

        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("today", LocalDate.now());

        // 오늘 날짜면 daily_routines에서 조회, 과거 날짜면 routines_history에서 조회
        if (selectedDate.equals(LocalDate.now())) {
            // 오늘 날짜 - 현재 진행중인 루틴들 조회
            Map<String, List<DailyRoutineDto>> routines = dailyRoutineService.getRoutinesByDate(userId, selectedDate);
            model.addAttribute("activeRoutines", routines.get("active"));
            model.addAttribute("completedRoutines", routines.get("completed"));
            model.addAttribute("passedRoutines", routines.get("passed"));
            model.addAttribute("isHistoryView", false);

        } else {
            // 과거 날짜 - 히스토리에서 조회
            List<RoutineHistoryDto> histories = routineHistoryService.getHistoryByDate(userId, selectedDate);

            // 상태별로 분류
            List<RoutineHistoryDto> activeHistories = histories.stream()
                .filter(h -> "ACTIVE".equals(h.getStatus()))
                .toList();
            List<RoutineHistoryDto> completedHistories = histories.stream()
                .filter(h -> "COMPLETED".equals(h.getStatus()))
                .toList();
            List<RoutineHistoryDto> passedHistories = histories.stream()
                .filter(h -> "PASS".equals(h.getStatus()))
                .toList();

            model.addAttribute("activeHistories", activeHistories);
            model.addAttribute("completedHistories", completedHistories);
            model.addAttribute("passedHistories", passedHistories);
            model.addAttribute("isHistoryView", true);
        }

        return "routine/routine-list";
    }


    //루틴 추가 폼 (모달)
    @GetMapping("/new")
    public String addRoutineForm(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            // 사용자 정보 추가
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
        } catch (Exception e) {
            log.error("사용자 정보 조회 실패: ", e);
            return "redirect:/signin";
        }

        model.addAttribute("routine", new DailyRoutineDto());
        return "routine/routine-form";
    }


    //루틴 추가
    @PostMapping
    public String createRoutine(
        @ModelAttribute DailyRoutineDto routineDto,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            dailyRoutineService.createRoutine(routineDto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "루틴이 성공적으로 추가되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "루틴 추가 중 오류가 발생했습니다.");
            log.error("Error creating routine", e);
        }

        return "redirect:/routines";
    }


    // 루틴 수정 폼 (모달) - JSON 응답
    @GetMapping("/{id}/edit")
    @ResponseBody
    public DailyRoutineDto editRoutineForm(
        @PathVariable Long id,
        HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not authenticated");
        }

        try {
            return dailyRoutineService.getRoutineById(id, userId);
        } catch (Exception e) {
            log.error("Error loading routine for edit", e);
            throw e;
        }
    }


    //루틴 수정
    @PatchMapping("/{id}")
    public String updateRoutine(
        @PathVariable Long id,
        @ModelAttribute DailyRoutineDto routineDto,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            dailyRoutineService.updateRoutine(id, routineDto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "루틴이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "루틴 수정 중 오류가 발생했습니다.");
            log.error("Error updating routine", e);
        }

        return "redirect:/routines";
    }


    // 루틴 삭제 - JSON 응답
    @DeleteMapping("/{id}")
    @ResponseBody
    public Map<String, Object> deleteRoutine(
        @PathVariable Long id,
        HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Map.of("success", false, "message", "인증이 필요합니다.");
        }

        try {
            dailyRoutineService.deleteRoutine(id, userId);
            return Map.of("success", true, "message", "루틴이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("Error deleting routine", e);
            return Map.of("success", false, "message", "루틴 삭제 중 오류가 발생했습니다.");
        }
    }

    //루틴 완료
    @PatchMapping("/{id}/complete")
    public String completeRoutine(
        @PathVariable Long id,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            dailyRoutineService.completeRoutine(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "루틴이 완료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "루틴 완료 처리 중 오류가 발생했습니다.");
            log.error("Error completing routine", e);
        }

        return "redirect:/routines";
    }

    //루틴 완료 취소
    @PatchMapping("/{id}/cancel")
    public String cancelCompletion(
        @PathVariable Long id,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            dailyRoutineService.cancelCompletion(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "루틴 완료가 취소되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "루틴 완료 취소 중 오류가 발생했습니다.");
            log.error("Error canceling routine completion", e);
        }

        return "redirect:/routines";
    }

    // 루틴 쉬어가기 (스킵)
    @PatchMapping("/{id}/skip")
    public String skipRoutine(
        @PathVariable Long id,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            dailyRoutineService.passRoutine(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "루틴을 쉬어가기로 설정했습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "루틴 스킵 처리 중 오류가 발생했습니다.");
            log.error("Error skipping routine", e);
        }

        return "redirect:/routines";
    }

    // 루틴 타이머 페이지
    @GetMapping("/{id}/timer")
    public String routineTimer(
        @PathVariable Long id,
        HttpSession session,
        Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            // 사용자 정보 추가
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);

            // 루틴 정보 조회
            DailyRoutineDto routine = dailyRoutineService.getRoutineById(id, userId);

            // 포모도로 세션 시작
            PomodoroSessionDto pomodoroSession = dailyRoutineService.startPomodoroSession(id, userId);

            // 다음 루틴 정보 조회 (있을 경우)
            DailyRoutineDto nextRoutine = dailyRoutineService.getNextRoutine(id, userId);

            // 모델에 필요한 모든 데이터 추가
            model.addAttribute("routine", routine);
            model.addAttribute("pomodoroSession", pomodoroSession);
            model.addAttribute("nextRoutine", nextRoutine);

            return "routine/timer";
        } catch (Exception e) {
            log.error("Error loading timer page", e);
            return "redirect:/routines";
        }
    }

    @PatchMapping("/timer/complete")
    public String completeTimer(
        @RequestParam Long sessionId,
        @RequestParam Long routineId,
        @RequestParam(required = false) Long nextRoutineId,
        @RequestParam(required = false) Integer actualMinutes,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            if (actualMinutes != null && actualMinutes >= 0) {
                // 세션의 실제 진행 시간 업데이트
                PomodoroSession pomodoroSession = pomodoroSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found"));

                PomodoroSession updatedSession = PomodoroSession.builder()
                    .id(pomodoroSession.getId())
                    .routine(pomodoroSession.getRoutine())
                    .durationMinutes(actualMinutes)
                    .startedAt(pomodoroSession.getStartedAt())
                    .endedAt(LocalDateTime.now())
                    .build();

                pomodoroSessionRepository.save(updatedSession);
                dailyRoutineService.updateActualFocusTime(routineId, actualMinutes, userId);
            } else {
                // 기존 방식으로 세션 종료
                dailyRoutineService.endPomodoroSession(sessionId, userId);
            }

            // 루틴 완료 처리
            dailyRoutineService.completeRoutine(routineId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "루틴이 성공적으로 완료되었습니다!");

            // 다음 루틴이 있는 경우 해당 루틴의 타이머 페이지로 이동
            if (nextRoutineId != null) {
                return "redirect:/routines/" + nextRoutineId + "/timer";
            }
        } catch (Exception e) {
            log.error("타이머 완료 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "타이머 완료 처리에 실패했습니다.");
        }

        return "redirect:/routines";
    }

    @PatchMapping("/timer/complete-coding")
    public String completeCodingTimer(
        @RequestParam Long sessionId,
        @RequestParam Long routineId,
        @RequestParam(required = false) Integer actualMinutes,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            // 코딩테스트 루틴인지 확인
            DailyRoutineDto routine = dailyRoutineService.getRoutineById(routineId, userId);

            if (dailyRoutineService.isCodingTestRoutine(routine)) {
                if (actualMinutes != null && actualMinutes >= 0) {
                    dailyRoutineService.updateActualFocusTime(routineId, actualMinutes, userId);
                }
                // 코딩테스트 루틴이면 회고 페이지로 이동
                return "redirect:/routines/" + routineId + "/coding-review?sessionId=" + sessionId;
            } else {
                // 일반 루틴이면 기존 로직
                return completeTimer(sessionId, routineId, null, actualMinutes, session, redirectAttributes);
            }
        } catch (Exception e) {
            log.error("타이머 완료 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "타이머 완료 처리에 실패했습니다.");
            return "redirect:/routines";
        }
    }

    @PatchMapping("/timer/complete-interview")
    public String completeInterviewTimer(
        @RequestParam Long sessionId,
        @RequestParam Long routineId,
        @RequestParam(required = false) Integer actualMinutes,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            // 면접준비 루틴인지 확인
            DailyRoutineDto routine = dailyRoutineService.getRoutineById(routineId, userId);

            if (dailyRoutineService.isInterviewRoutine(routine)) {
                if (actualMinutes != null && actualMinutes >= 0) {
                    dailyRoutineService.updateActualFocusTime(routineId, actualMinutes, userId);
                }
                // 면접준비 루틴이면 회고 페이지로 이동
                return "redirect:/routines/" + routineId + "/interview-review?sessionId=" + sessionId;
            } else {
                // 일반 루틴이면 기존 로직
                return completeTimer(sessionId, routineId, null, actualMinutes, session, redirectAttributes);
            }
        } catch (Exception e) {
            log.error("타이머 완료 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "타이머 완료 처리에 실패했습니다.");
            return "redirect:/routines";
        }
    }

    @GetMapping("/{id}/coding-review")
    public String showCodingReviewPage(
        @PathVariable Long id,
        @RequestParam(required = false) Long sessionId,
        HttpSession session,
        Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            // 사용자 정보 추가
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);

            DailyRoutineDto routine = dailyRoutineService.getRoutineById(id, userId);

            // 코딩테스트 루틴인지 확인
            if (!dailyRoutineService.isCodingTestRoutine(routine)) {
                return "redirect:/routines";
            }

            // 기존 회고가 있는지 확인
            List<CodingTestReviewDto> existingReviews = dailyRoutineService.getCodingTestReviews(id, userId);

            model.addAttribute("routine", routine);
            model.addAttribute("existingReviews", existingReviews);
            model.addAttribute("sessionId", sessionId);
            model.addAttribute("isViewMode", !existingReviews.isEmpty());

            return "routine/coding-review";
        } catch (Exception e) {
            log.error("코딩테스트 회고 페이지 로드 중 오류 발생", e);
            return "redirect:/routines";
        }
    }

    @PostMapping("/{id}/coding-review")
    public String saveCodingReview(
        @PathVariable Long id,
        @RequestParam("problemTitles") List<String> problemTitles,
        @RequestParam("problemDescriptions") List<String> problemDescriptions,
        @RequestParam("mySolutions") List<String> mySolutions,
        @RequestParam("correctSolutions") List<String> correctSolutions,
        @RequestParam(required = false) Long sessionId,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            DailyRoutineDto routine = dailyRoutineService.getRoutineById(id, userId);

            // 회고 DTO 리스트 생성
            List<CodingTestReviewDto> reviewDtos = new ArrayList<>();
            for (int i = 0; i < problemTitles.size(); i++) {
                if (i < problemDescriptions.size() && i < mySolutions.size() &&
                    i < correctSolutions.size()) {
                    reviewDtos.add(CodingTestReviewDto.builder()
                        .problemTitle(problemTitles.get(i))
                        .problemDescription(problemDescriptions.get(i))
                        .mySolution(mySolutions.get(i))
                        .correctSolution(correctSolutions.get(i))
                        .problemType(routine.getCategory())
                        .build());
                }
            }

            // 세션이 있으면 종료 처리
            if (sessionId != null) {
                dailyRoutineService.endPomodoroSession(sessionId, userId);
            }

            // 회고와 함께 루틴 완료
            dailyRoutineService.completeCodingTestRoutine(id, userId, reviewDtos);

            redirectAttributes.addFlashAttribute("successMessage", "코딩테스트 회고가 저장되고 루틴이 완료되었습니다!");

        } catch (Exception e) {
            log.error("코딩테스트 회고 저장 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "회고 저장에 실패했습니다.");
        }

        return "redirect:/routines";
    }

    @GetMapping("/{id}/interview-review")
    public String showInterviewReviewPage(
        @PathVariable Long id,
        @RequestParam(required = false) Long sessionId,
        HttpSession session,
        Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            // 사용자 정보 추가
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);

            DailyRoutineDto routine = dailyRoutineService.getRoutineById(id, userId);

            // 면접준비 루틴인지 확인
            if (!dailyRoutineService.isInterviewRoutine(routine)) {
                return "redirect:/routines";
            }

            // 기존 회고가 있는지 확인
            List<InterviewReviewDto> existingReviews = dailyRoutineService.getInterviewReviews(id, userId);

            model.addAttribute("routine", routine);
            model.addAttribute("existingReviews", existingReviews);
            model.addAttribute("sessionId", sessionId);
            model.addAttribute("isViewMode", !existingReviews.isEmpty());

            return "routine/interview-review";
        } catch (Exception e) {
            log.error("면접준비 회고 페이지 로드 중 오류 발생", e);
            return "redirect:/routines";
        }
    }

    @PostMapping("/{id}/interview-review")
    public String saveInterviewReview(
        @PathVariable Long id,
        @RequestParam("studyContents") List<String> studyContents,
        @RequestParam("difficultParts") List<String> difficultParts,
        @RequestParam("nextStudyPlans") List<String> nextStudyPlans,
        @RequestParam(required = false) Long sessionId,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            DailyRoutineDto routine = dailyRoutineService.getRoutineById(id, userId);

            // 회고 DTO 리스트 생성
            List<InterviewReviewDto> reviewDtos = new ArrayList<>();
            for (int i = 0; i < studyContents.size(); i++) {
                if (i < studyContents.size() &&
                    i < difficultParts.size() && i < nextStudyPlans.size()) {
                    reviewDtos.add(InterviewReviewDto.builder()
                        .techCategory(routine.getCategory())
                        .studyContent(studyContents.get(i))
                        .difficultParts(difficultParts.get(i))
                        .nextStudyPlan(nextStudyPlans.get(i))
                        .build());
                }
            }

            // 세션이 있으면 종료 처리
            if (sessionId != null) {
                dailyRoutineService.endPomodoroSession(sessionId, userId);
            }

            // 회고와 함께 루틴 완료
            dailyRoutineService.completeInterviewRoutine(id, userId, reviewDtos);

            redirectAttributes.addFlashAttribute("successMessage", "면접준비 회고가 저장되고 루틴이 완료되었습니다!");

        } catch (Exception e) {
            log.error("면접준비 회고 저장 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "회고 저장에 실패했습니다.");
        }

        return "redirect:/routines";
    }

    @GetMapping("/history/{routineId}/coding-review")
    public String showHistoryCodingReviewPage(
        @PathVariable Long routineId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        HttpSession session,
        Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            // 사용자 정보 추가
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);

            // 히스토리에서 루틴 정보 조회
            List<RoutineHistoryDto> routineHistories = routineHistoryService.getHistoryByDate(userId, date);
            RoutineHistoryDto routineHistory = routineHistories.stream()
                .filter(h -> h.getOriginalRoutineId().equals(routineId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Routine history not found"));

            // 코딩테스트 회고 히스토리 조회
            List<CodingTestReviewHistoryDto> reviewHistories = routineHistoryService
                .getCodingTestReviewHistoryByRoutineId(routineId, date);

            model.addAttribute("routine", routineHistory);
            model.addAttribute("reviewHistories", reviewHistories);
            model.addAttribute("isHistoryView", true);
            model.addAttribute("routineDate", date);

            return "routine/coding-review-history";
        } catch (Exception e) {
            log.error("히스토리 코딩테스트 회고 페이지 로드 중 오류 발생", e);
            return "redirect:/routines?date=" + date;
        }
    }

    @GetMapping("/history/{routineId}/interview-review")
    public String showHistoryInterviewReviewPage(
        @PathVariable Long routineId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        HttpSession session,
        Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }

        try {
            // 사용자 정보 추가
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);

            // 히스토리에서 루틴 정보 조회
            List<RoutineHistoryDto> routineHistories = routineHistoryService.getHistoryByDate(userId, date);
            RoutineHistoryDto routineHistory = routineHistories.stream()
                .filter(h -> h.getOriginalRoutineId().equals(routineId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Routine history not found"));

            // 면접준비 회고 히스토리 조회
            List<InterviewReviewHistoryDto> reviewHistories = routineHistoryService
                .getInterviewReviewHistoryByRoutineId(routineId, date);

            model.addAttribute("routine", routineHistory);
            model.addAttribute("reviewHistories", reviewHistories);
            model.addAttribute("isHistoryView", true);
            model.addAttribute("routineDate", date);

            return "routine/interview-review-history";
        } catch (Exception e) {
            log.error("히스토리 면접준비 회고 페이지 로드 중 오류 발생", e);
            return "redirect:/routines?date=" + date;
        }
    }

    @GetMapping("/history/pomodoro")
    @ResponseBody
    public List<PomodoroSessionHistoryDto> getPomodoroHistoryByDate(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not authenticated");
        }

        return routineHistoryService.getPomodoroSessionHistoryByDate(userId, date);
    }

    /**
     * 특정 루틴의 포모도로 세션 히스토리 조회
     */
    @GetMapping("/history/{routineId}/pomodoro")
    @ResponseBody
    public List<PomodoroSessionHistoryDto> getPomodoroHistoryByRoutine(
        @PathVariable Long routineId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not authenticated");
        }

        return routineHistoryService.getPomodoroSessionHistoryByRoutineId(routineId, date);
    }

    /**
     * 포모도로 세션 통계 조회 (API 엔드포인트)
     */
    @GetMapping("/stats/pomodoro")
    @ResponseBody
    public Map<String, Object> getPomodoroStats(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not authenticated");
        }

        Map<String, Object> stats = new HashMap<>();

        // 총 집중 시간
        Integer totalMinutes = routineHistoryService.getTotalFocusTimeFromHistory(userId);
        stats.put("totalFocusMinutes", totalMinutes);
        stats.put("totalFocusHours", totalMinutes != null ? totalMinutes / 60.0 : 0);

        // 일별 집중 시간
        List<Object[]> dailyStats = routineHistoryService.getDailyFocusTimeFromHistory(userId);
        stats.put("dailyFocusTime", dailyStats);

        // 월별 집중 시간
        List<Object[]> monthlyStats = routineHistoryService.getMonthlyFocusTimeFromHistory(userId);
        stats.put("monthlyFocusTime", monthlyStats);

        // 요일별 평균 집중 시간
        List<Object[]> weeklyStats = routineHistoryService.getWeeklyAverageFocusTimeFromHistory(userId);
        stats.put("weeklyAverageFocusTime", weeklyStats);

        // 완료율
        List<Object[]> completionStats = routineHistoryService.getCompletionRate(userId);
        if (!completionStats.isEmpty()) {
            Object[] data = completionStats.get(0);
            Long totalSessions = (Long) data[0];
            Long completedSessions = (Long) data[1];
            double completionRate = totalSessions > 0 ? (completedSessions.doubleValue() / totalSessions.doubleValue()) * 100 : 0;
            stats.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
            stats.put("totalSessions", totalSessions);
            stats.put("completedSessions", completedSessions);
        }

        return stats;
    }


    // 테스트용 - 수동 아카이브 실행
    // 실제 운영에서는 제거하거나 관리자 권한으로 제한
    @PostMapping("/admin/archive")
    @ResponseBody
    public String manualArchive(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            routineScheduler.manualArchive(date);
            return "Archive completed for date: " + date;
        } catch (Exception e) {
            log.error("Manual archive failed", e);
            return "Archive failed: " + e.getMessage();
        }
    }


    //테스트용 - 히스토리 조회
    @GetMapping("/admin/history")
    @ResponseBody
    public List<RoutineHistoryDto> getHistory(
        @RequestParam Long userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return routineHistoryService.getHistoryByDate(userId, date);
    }
}