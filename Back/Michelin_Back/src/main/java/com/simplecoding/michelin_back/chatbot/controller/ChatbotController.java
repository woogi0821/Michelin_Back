package com.simplecoding.michelin_back.chatbot.controller;

import com.simplecoding.michelin_back.chatbot.dto.ChatbotDto;
import com.simplecoding.michelin_back.chatbot.service.ChatbotService;
import com.simplecoding.michelin_back.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatbotDto.ChatResponse>> chat(
            @RequestBody ChatbotDto.ChatRequest req) {
        return ResponseEntity.ok(ApiResponse.success(chatbotService.chat(req)));
    }
}
