package com.codemap.core.interview.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class QuestionSummaryDto {

    private Long id;
    private String category;
    private String difficulty;
    private String questionText;

    public QuestionSummaryDto(Long questionId, String questionText, String category, String difficulty) {
        this.id = questionId;
        this.questionText = questionText;
        this.category = category;
        this.difficulty = difficulty;
    }

}