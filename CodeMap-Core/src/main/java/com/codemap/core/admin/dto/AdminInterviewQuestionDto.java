package com.codemap.core.admin.dto;

import com.codemap.core.interview.domain.InterviewQuestion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminInterviewQuestionDto {
    private Long id;
    private String category;
    private String difficulty;
    private String questionText;
    private String answerText;

    public AdminInterviewQuestionDto() {}

    public static AdminInterviewQuestionDto from(InterviewQuestion entity) {
        AdminInterviewQuestionDto dto = new AdminInterviewQuestionDto();
        dto.setId(entity.getId());
        dto.setCategory(entity.getCategory());
        dto.setDifficulty(entity.getDifficulty());
        dto.setQuestionText(entity.getQuestionText());
        dto.setAnswerText(entity.getAnswerText());
        return dto;
    }

    public InterviewQuestion toEntity() {
        return InterviewQuestion.builder()
            .id(this.id)
            .category(this.category)
            .difficulty(this.difficulty)
            .questionText(this.questionText)
            .answerText(this.answerText)
            .build();
    }
}