package com.codemap.core.chatbot.controller;

import com.codemap.core.chatbot.dto.ChatRequestDto;
import com.codemap.core.chatbot.dto.ChatResponseDto;
import com.codemap.core.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ChatResponseDto handleChat(@RequestBody ChatRequestDto requestDto) {
        String response = chatbotService.getRecommendation(requestDto.getKeyword());
        return new ChatResponseDto(response);
    }
}