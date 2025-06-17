package com.codemap.core.interview.repository;

import com.codemap.core.interview.domain.InterviewQuestion;
import com.codemap.core.interview.domain.UserAnswer;
import com.codemap.core.interview.dto.QuestionSummaryDto;
import com.codemap.core.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    Optional<UserAnswer> findTopByUserAndQuestionOrderByIdDesc(User user, InterviewQuestion question);

    void deleteByQuestion(InterviewQuestion question);

    // 사용자의 모든 답변 다 가져옴.
    @EntityGraph(attributePaths = "question")
    List<UserAnswer> findByUserIdOrderByIdDesc(Long userId);

    @Query("""
                SELECT new com.codemap.core.interview.dto.QuestionSummaryDto(
                    ua.question.id,
                    ua.question.questionText,
                    ua.question.category,
                    ua.question.difficulty
                )
                FROM UserAnswer ua
                WHERE ua.user.id = :userId
                  AND ua.answeredAt = (
                      SELECT MAX(subUa.answeredAt)
                      FROM UserAnswer subUa
                      WHERE subUa.user.id = :userId
                        AND subUa.question.id = ua.question.id
                  )
            """)
    List<QuestionSummaryDto> findLatestAnswersByUserId(@Param("userId") Long userId);
}