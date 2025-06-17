package com.codemap.core.interview.controller;

import com.codemap.core.interview.domain.InterviewQuestion;
import com.codemap.core.interview.domain.UserAnswer;
import com.codemap.core.interview.dto.QuestionSummaryDto;
import com.codemap.core.interview.service.InterviewService;
import com.codemap.core.interview.service.KeywordCompareService;
import com.codemap.core.interview.service.UserAnswerService;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/interview/history")
public class InterviewHistoryController {

    private final UserService userService;
    private final UserAnswerService userAnswerService;
    private final InterviewService interviewService;
    private final KeywordCompareService keywordCompareService;


    @GetMapping
    public String showUserAnswerHistory(@SessionAttribute("userId") Long userId, Model model) {
        List<QuestionSummaryDto> questions = userAnswerService.getAnsweredQuestionsByUserId(userId);
        model.addAttribute("answeredQuestions", questions);

        System.out.println("답변한 질문 수: " + questions.size());
        questions.forEach(q -> System.out.println(q.getQuestionText()));

        if (userId != null) {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
        }

        return "interview/interview-history";
    }

    @GetMapping("/{questionId}")
    public String showDetailedAnswer(@PathVariable Long questionId,
        @SessionAttribute("userId") Long userId,
        HttpSession session,
        Model model) {

        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/user/signin"; // 비로그인 사용자는 로그인 페이지로
        }

        // 사용자, 질문, 답변 조회
        InterviewQuestion question = interviewService.findById(questionId);
        User user = userService.getUserById(sessionUserId);  // 현재 로그인한 사용자
        UserAnswer userAnswer = userAnswerService.findLatestAnswer(user, question); // 내 답변만 조회

        if (userAnswer == null || !userAnswer.getUser().getId().equals(sessionUserId)) {
            throw new AccessDeniedException("본인의 답변만 조회할 수 있습니다.");
        }

        // 원본 답변 텍스트와 모범 답안, 키워드
        String userAnswerText = userAnswer.getAnswerText();
        String modelAnswer = question.getAnswerText();
        String dbKeywords = question.getKeywords();

        // 키워드 분석 및 평가 생성
        // ✅ 1. 핵심 키워드 추출 (DB 기반 또는 기타 방식)
        List<String> coreKeywords = keywordCompareService.extractCoreKeywords(dbKeywords);

        // ✅ 2. 사용자 답변 분석
        List<String> matchedKeywords = keywordCompareService.findMatchedKeywords(userAnswerText, coreKeywords);
        List<String> missingKeywords = keywordCompareService.findMissingKeywords(userAnswerText, coreKeywords);

        // ✅ 3. 정확도 직접 계산 (퍼센트 단위)
        int matchedCount = matchedKeywords.size();
        int totalCount = coreKeywords.size();
        double accuracy = totalCount > 0 ? ((double) matchedCount / totalCount) * 100.0 : 0.0;

        // ✅ 4. 등급 및 평가 문장 생성
        String grade = keywordCompareService.getGradeByAccuracy(accuracy);
        String comment = keywordCompareService.generateEvaluationComment(accuracy, totalCount, matchedCount);

        // ✅ 5. 모범 답안 하이라이팅
        String highlightedModelAnswer = keywordCompareService.generateHighlightedAnswer(
            modelAnswer, matchedKeywords, missingKeywords
        );

        // 분석 결과 Map 구성
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalKeywords", coreKeywords.size());
        analysis.put("matchedKeywords", matchedKeywords);
        analysis.put("missingKeywords", missingKeywords);
        analysis.put("matchedCount", matchedKeywords.size());
        analysis.put("missingCount", missingKeywords.size());
        analysis.put("accuracy", accuracy); // 퍼센트 단위
        analysis.put("grade", grade);
        analysis.put("comment", comment);

        // 모델에 전달
        model.addAttribute("question", question);
        model.addAttribute("userAnswer", userAnswer);
        model.addAttribute("analysis", analysis);
        model.addAttribute("highlightedModelAnswer", highlightedModelAnswer);
        model.addAttribute("coreKeywords", coreKeywords);
        model.addAttribute("keywordSource", (dbKeywords != null && !dbKeywords.trim().isEmpty()) ? "DB" : "EXTRACTED");
        model.addAttribute("isHistoryView", true);
        model.addAttribute("category", question.getCategory());


        if (userId != null) {
            model.addAttribute("user", user);
        }

        System.out.println("분석 결과: " + analysis);

        return "interview/interview-result";

    }
}