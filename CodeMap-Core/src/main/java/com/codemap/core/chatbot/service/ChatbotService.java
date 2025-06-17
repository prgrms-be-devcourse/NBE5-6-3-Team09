package com.codemap.core.chatbot.service;

import com.codemap.core.chatbot.llm.Assistant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final Assistant assistant;

    public String getRecommendation(String keyword) {
        return assistant.recommendPlan(keyword);
    }
}