package com.codemap.core.mypage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStatDto {

    private Integer totalFocusMinutes;          // 총 순공 시간
    private Integer totalActualFocusMinutes;  // 실제 순공 시간
    private String dailyCompletionRateJson; // 카테고리별 완료율(Json)
    private List<String> chartDates;
    private List<Integer> chartFocusMinutes;
    private List<Integer> chartActualFocusMinutes; // 실제 시간 차트용
}