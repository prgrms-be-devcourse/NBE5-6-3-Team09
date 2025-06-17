package com.codemap.core.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoutineCategoryCompletionDto {

    private String category;
    private long completedCount;
    private long totalCount;

    public double getCompletionRate() {
        return totalCount == 0 ? 0 : ((double) completedCount / totalCount) * 100;
    }
}