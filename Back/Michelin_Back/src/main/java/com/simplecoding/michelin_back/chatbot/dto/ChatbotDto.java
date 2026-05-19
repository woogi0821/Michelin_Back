package com.simplecoding.michelin_back.chatbot.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ChatbotDto {

    /** 프론트 → 스프링: 사용자 메시지 전송 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequest {
        private String message;
        private Long sessionId;  // null이면 새 세션 생성
    }

    /** 스프링 → 프론트: 챗봇 응답 */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class ChatResponse {
        private Long sessionId;
        private String answer;
        private LocalDateTime timestamp;
    }

    /** 대화 이력 단건 */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class MessageResponse {
        private Long messageId;
        private String role;
        private String content;
        private LocalDateTime insertTime;
    }

    /** 세션 목록 */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class SessionResponse {
        private Long sessionId;
        private LocalDateTime insertTime;
        private LocalDateTime updateTime;
    }

    // ── Python 서버 연동용 내부 DTO ─────────────────────────────

    /** 스프링 → Python 전송 바디 */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class PythonRequest {
        private String message;
        private List<PythonHistory> history;
    }

    /** Python 히스토리 포맷: role = user / bot */
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class PythonHistory {
        private String role;    // "user" or "bot"
        private String content;
    }

    /** Python → 스프링 응답 바디 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PythonResponse {
        private String answer;
    }
}
