package com.simplecoding.michelin_back.chatbot.service;

import com.simplecoding.michelin_back.chatbot.dto.ChatbotDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    @Value("${chatbot.server.url}")
    private String chatbotServerUrl;

    private final RestTemplate restTemplate;

    public ChatbotDto.ChatResponse chat(ChatbotDto.ChatRequest req) {
        String flaskUrl = chatbotServerUrl + "/chat";

        ChatbotDto.FlaskRequest flaskRequest = ChatbotDto.FlaskRequest.builder()
                .message(req.getMessage())
                .build();

        try {
            ChatbotDto.FlaskResponse flaskResponse = restTemplate.postForObject(
                    flaskUrl, flaskRequest, ChatbotDto.FlaskResponse.class);

            String answer = (flaskResponse != null && flaskResponse.getAnswer() != null)
                    ? flaskResponse.getAnswer()
                    : "현재 챗봇 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해주세요.";

            return ChatbotDto.ChatResponse.builder()
                    .answer(answer)
                    .sessionId(req.getSessionId())
                    .build();

        } catch (Exception e) {
            log.error("[ChatbotService] Flask 서버 호출 실패: {}", e.getMessage());
            return ChatbotDto.ChatResponse.builder()
                    .answer("현재 챗봇 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해주세요.")
                    .sessionId(req.getSessionId())
                    .build();
        }
    }
}
