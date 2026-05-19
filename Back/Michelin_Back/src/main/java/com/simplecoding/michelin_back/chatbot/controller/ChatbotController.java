package com.simplecoding.michelin_back.chatbot.controller;

import com.simplecoding.michelin_back.chatbot.dto.ChatbotDto;
import com.simplecoding.michelin_back.chatbot.service.ChatbotService;
import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    /** 메시지 전송 */
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatbotDto.ChatResponse>> chat(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ChatbotDto.ChatRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                chatbotService.chat(user.getMemberId(), req)));
    }

    /** 내 세션 목록 */
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<ChatbotDto.SessionResponse>>> sessions(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(
                chatbotService.getSessions(user.getMemberId())));
    }

    /** 세션별 대화 이력 */
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<List<ChatbotDto.MessageResponse>>> messages(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(
                chatbotService.getMessages(user.getMemberId(), sessionId)));
    }
}
