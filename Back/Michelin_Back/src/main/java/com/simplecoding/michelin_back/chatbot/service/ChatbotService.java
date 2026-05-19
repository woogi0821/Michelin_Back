package com.simplecoding.michelin_back.chatbot.service;

import com.simplecoding.michelin_back.chatbot.dto.ChatbotDto;
import com.simplecoding.michelin_back.chatbot.entity.ChatbotMessage;
import com.simplecoding.michelin_back.chatbot.entity.ChatbotSession;
import com.simplecoding.michelin_back.chatbot.repository.ChatbotMessageRepository;
import com.simplecoding.michelin_back.chatbot.repository.ChatbotSessionRepository;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatbotSessionRepository sessionRepository;
    private final ChatbotMessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Value("${chatbot.server.url}")
    private String chatbotServerUrl;

    @Transactional
    public ChatbotDto.Response chat(CustomUserDetails userDetails, ChatbotDto.Request request) {

        // 1. 회원 조회
        Member member = memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));

        // 2. 세션 조회 또는 신규 생성
        ChatbotSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 세션입니다."));
            // 본인 세션인지 검증
            if (!session.getMember().getMemberId().equals(userDetails.getMemberId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없는 세션입니다.");
            }
        } else {
            session = sessionRepository.save(ChatbotSession.builder().member(member).build());
        }

        // 3. DB에서 대화 이력 조회 → Python history 포맷으로 변환
        //    DB ROLE: USER/ASSISTANT → Python sender: USER/BOT
        List<Map<String, String>> history = messageRepository
                .findBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId())
                .stream()
                .map(msg -> Map.of(
                        "sender", "ASSISTANT".equals(msg.getRole()) ? "BOT" : "USER",
                        "text", msg.getContent()
                ))
                .collect(Collectors.toList());

        // 4. Python Flask 호출
        ChatbotDto.PythonRequest pythonRequest = ChatbotDto.PythonRequest.builder()
                .current_message(request.getMessage())
                .history(history)
                .build();

        ChatbotDto.PythonResponse pythonResponse;
        try {
            pythonResponse = restTemplate.postForObject(
                    chatbotServerUrl + "/api/chat",
                    pythonRequest,
                    ChatbotDto.PythonResponse.class
            );
        } catch (Exception e) {
            log.error("[Chatbot] Python 서버 호출 실패: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "챗봇 서버에 연결할 수 없습니다.");
        }

        if (pythonResponse == null || pythonResponse.getReply() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "챗봇 응답을 받지 못했습니다.");
        }

        // 5. 유저 메시지 + 봇 응답 DB 저장
        messageRepository.save(ChatbotMessage.builder()
                .session(session)
                .role("USER")
                .content(request.getMessage())
                .tokenUsed(0L)
                .build());

        messageRepository.save(ChatbotMessage.builder()
                .session(session)
                .role("ASSISTANT")
                .content(pythonResponse.getReply())
                .tokenUsed(0L)
                .build());

        return ChatbotDto.Response.builder()
                .sessionId(session.getSessionId())
                .reply(pythonResponse.getReply())
                .build();
    }

    // 대화 이력 조회
    @Transactional(readOnly = true)
    public List<ChatbotMessage> getHistory(CustomUserDetails userDetails, Long sessionId) {
        ChatbotSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 세션입니다."));

        if (!session.getMember().getMemberId().equals(userDetails.getMemberId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없는 세션입니다.");
        }

        return messageRepository.findBySession_SessionIdOrderByCreatedAtAsc(sessionId);
    }
}
