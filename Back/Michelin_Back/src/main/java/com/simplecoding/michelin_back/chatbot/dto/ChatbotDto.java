package com.simplecoding.michelin_back.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class ChatbotDto {

    // 프론트 → 스프링 요청
    @Getter
    @NoArgsConstructor
    public static class Request {
        @NotBlank
        private String message;
        private Long sessionId; // null이면 새 세션 생성
    }

    // 스프링 → Python Flask 요청
    @Getter
    @Builder
    public static class PythonRequest {
        private String current_message;
        private List<Map<String, String>> history; // [{"sender":"USER","text":"..."}]
    }

    // Python Flask → 스프링 응답
    @Getter
    @NoArgsConstructor
    public static class PythonResponse {
        private String reply;
    }

    // 스프링 → 프론트 응답
    @Getter
    @Builder
    public static class Response {
        private Long sessionId;
        private String reply;
    }
}
