package com.codemap.core.chatbot.llm;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface Assistant {

    @SystemMessage("""
        너는 개발자 취업준비생들의 일정 플래너 챗봇이야.
                사용자가 상황 키워드를 주면, 해당 상황에 맞는 실용적인 활동이나 계획을 추천해줘.
        
                무관한 주제(예: 음식 추천, 날씨 등) 질문이 오면
                "저는 일정 추천 전용 챗봇이에요!" 라고 정중히 답변해.
        """)
    String recommendPlan(@UserMessage String keyword);
}