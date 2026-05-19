package com.simplecoding.michelin_back.chatbot.controller;

import com.simplecoding.michelin_back.chatbot.dto.ChatbotDto;
import com.simplecoding.michelin_back.chatbot.entity.ChatbotMessage;
import com.simplecoding.michelin_back.chatbot.service.ChatbotService;
import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // 로그인 사용자만 접근 가능
public class ChatbotController {

    private final ChatbotService chatbotService;

    // 메시지 전송
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatbotDto.Response>> chat(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChatbotDto.Request request) {
        ChatbotDto.Response response = chatbotService.chat(userDetails, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "챗봇 응답 성공", response, 0, 1));
    }

    // 대화 이력 조회
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<ApiResponse<List<ChatbotMessage>>> getHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long sessionId) {
        List<ChatbotMessage> history = chatbotService.getHistory(userDetails, sessionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "대화 이력 조회 성공", history, 0, history.size()));
    }
}
