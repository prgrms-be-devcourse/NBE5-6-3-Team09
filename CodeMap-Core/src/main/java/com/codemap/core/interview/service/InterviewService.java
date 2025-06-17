package com.codemap.core.interview.service;

import com.codemap.core.interview.domain.InterviewQuestion;
import com.codemap.core.interview.repository.InterviewRepository;
import com.codemap.core.interview.repository.UserAnswerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final UserAnswerRepository userAnswerRepository;

    // ✅ 사용자용: 전체 카테고리 목록 조회
    public List<String> getAllCategories() {
        return interviewRepository.findDistinctCategory();
    }

    // ✅ 사용자용: 카테고리 기반 랜덤 질문 3개 추출
    public List<InterviewQuestion> pickFiveRandomByCategories(List<String> categories) {
        Pageable limitFive = PageRequest.of(0, 5);
        return interviewRepository.findRandomFiveByCategoryIn(categories, limitFive);
    }



    // ✅ 사용자/관리자 공통: ID로 질문 찾기
    public InterviewQuestion findById(Long questionId) {
        return interviewRepository.findById(questionId).orElse(null);
    }

    // ✅ 관리자용: 질문 전체 조회
    public List<InterviewQuestion> findAll() {
        return interviewRepository.findAll();
    }

    // ✅ 관리자용: 질문 저장
    public void save(InterviewQuestion question) {
        log.info("DB 저장 시도: {}", question);
        interviewRepository.save(question);
    }

    // ✅ 관리자용: 질문 수정
    public void update(Long id, InterviewQuestion updatedData) {
        InterviewQuestion original = findById(id);
        original.updateFrom(updatedData);
        // 영속 상태에서 updateFrom()으로 필드 수정만 하면, 트랜잭션 커밋 시 자동 반영됨
    }

    // ✅ 관리자용: 질문 삭제
    public void deleteById(Long id) {
        InterviewQuestion question = findById(id);
        if (question != null) {
            userAnswerRepository.deleteByQuestion(question); // 🔥 관련 답변 먼저 삭제
            interviewRepository.deleteById(id);              // 🔥 그 후 질문 삭제
        }
    }
}