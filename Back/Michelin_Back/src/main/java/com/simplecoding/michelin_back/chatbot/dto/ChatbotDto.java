package com.simplecoding.michelin_back.chatbot.dto;

import lombok.*;

public class ChatbotDto {

    /** 프론트 → Spring 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequest {
        private String message;
        private Long sessionId;
    }

    /** Spring → 프론트 응답 */
    @Getter
    @Builder
    public static class ChatResponse {
        private String answer;
        private Long sessionId;
    }

    /** Spring → Flask 요청 */
    @Getter
    @Builder
    public static class FlaskRequest {
        private String message;
    }

    /** Flask → Spring 응답 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlaskResponse {
        private String answer;
    }
}
