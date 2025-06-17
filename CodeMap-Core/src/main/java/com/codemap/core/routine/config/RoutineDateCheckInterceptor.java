package com.codemap.core.routine.config;

import com.codemap.core.routine.service.RoutineHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutineDateCheckInterceptor implements HandlerInterceptor {

    private final RoutineHistoryService routineHistoryService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 루틴 관련 URL일 때만 체크
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/routines")) {
            HttpSession session = request.getSession();
            LocalDate today = LocalDate.now();

            // 세션에 저장된 마지막 접속 날짜 확인
            LocalDate lastAccessDate = (LocalDate) session.getAttribute("lastAccessDate");

            if (lastAccessDate == null || !lastAccessDate.equals(today)) {
                log.info("Date changed from {} to {}", lastAccessDate, today);

                // 날짜가 변경되었으면 이전 날짜들의 아카이브 확인
                if (lastAccessDate != null) {
                    LocalDate checkDate = lastAccessDate;
                    while (checkDate.isBefore(today)) {
                        routineHistoryService.checkAndArchiveIfNeeded(checkDate);
                        checkDate = checkDate.plusDays(1);
                    }
                } else {
                    // 첫 접속이면 어제 날짜만 확인
                    routineHistoryService.checkAndArchiveIfNeeded(today.minusDays(1));
                }

                // 현재 날짜를 세션에 저장
                session.setAttribute("lastAccessDate", today);
            }
        }

        return true;
    }
}