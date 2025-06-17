package com.codemap.core.interview.service;

import com.codemap.core.interview.domain.InterviewQuestion;
import com.codemap.core.interview.domain.UserAnswer;
import com.codemap.core.interview.dto.QuestionSummaryDto;
import com.codemap.core.interview.repository.UserAnswerRepository;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;
    private final UserRepository userRepository;

    // 사용자 답변 저장
    public UserAnswer saveUserAnswer(User user, InterviewQuestion question, String answerText) {
        UserAnswer userAnswer = UserAnswer.builder()
            .user(user)
            .question(question)
            .answerText(answerText)
            .answeredAt(LocalDateTime.now())
            .build();

        return userAnswerRepository.save(userAnswer);
    }

    // 사용자 답변 조회 (결과 보기용)
    public UserAnswer findLatestAnswer(User user, InterviewQuestion question) {
        return userAnswerRepository.findTopByUserAndQuestionOrderByIdDesc(user, question)
            .orElseThrow(() -> new RuntimeException("답변이 존재하지 않습니다."));
    }

    // 답변한 질문 목록 조회 (중복 없이 최신 것만)
    public List<QuestionSummaryDto> getAnsweredQuestionsByUserId(Long userId) {
        return userAnswerRepository.findLatestAnswersByUserId(userId);
    }
}