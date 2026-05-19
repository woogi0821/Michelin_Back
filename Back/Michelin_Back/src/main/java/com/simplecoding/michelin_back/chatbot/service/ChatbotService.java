package com.simplecoding.michelin_back.chatbot.service;

import com.simplecoding.michelin_back.chatbot.dto.ChatbotDto;
import com.simplecoding.michelin_back.chatbot.entity.ChatbotMessage;
import com.simplecoding.michelin_back.chatbot.entity.ChatbotSession;
import com.simplecoding.michelin_back.chatbot.repository.ChatbotMessageRepository;
import com.simplecoding.michelin_back.chatbot.repository.ChatbotSessionRepository;
import com.simplecoding.michelin_back.common.CommonException;
import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotService {

    private final ChatbotSessionRepository sessionRepository;
    private final ChatbotMessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Value("${chatbot.server.url}")
    private String chatbotServerUrl;

    /**
     * 메시지 전송 → Python 프록시 → DB 저장 → 응답 반환
     */
    @Transactional
    public ChatbotDto.ChatResponse chat(Long memberId, ChatbotDto.ChatRequest req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));

        // 세션 조회 또는 신규 생성
        ChatbotSession session;
        if (req.getSessionId() != null) {
            session = sessionRepository.findById(req.getSessionId())
                    .orElseThrow(() -> CommonException.notFound("세션을 찾을 수 없습니다."));
        } else {
            session = sessionRepository.save(
                    ChatbotSession.builder().member(member).build());
        }

        // 최근 20개 대화 이력 조회 (Python 히스토리 포맷 변환)
        List<ChatbotDto.PythonHistory> history = messageRepository
                .findTop20BySession_SessionIdOrderByInsertTimeAsc(session.getSessionId())
                .stream()
                .map(m -> ChatbotDto.PythonHistory.builder()
                        .role("ASSISTANT".equals(m.getRole()) ? "bot" : "user")
                        .content(m.getContent())
                        .build())
                .collect(Collectors.toList());

        // 사용자 메시지 DB 저장
        messageRepository.save(ChatbotMessage.builder()
                .session(session)
                .role("USER")
                .content(req.getMessage())
                .build());

        // Python 서버 호출
        String answer = callPythonServer(req.getMessage(), history);

        // 챗봇 응답 DB 저장
        messageRepository.save(ChatbotMessage.builder()
                .session(session)
                .role("ASSISTANT")
                .content(answer)
                .build());

        return ChatbotDto.ChatResponse.builder()
                .sessionId(session.getSessionId())
                .answer(answer)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /** Python Flask 서버로 메시지 전송 */
    private String callPythonServer(String message, List<ChatbotDto.PythonHistory> history) {
        ChatbotDto.PythonRequest payload = ChatbotDto.PythonRequest.builder()
                .message(message)
                .history(history)
                .build();
        try {
            ResponseEntity<ChatbotDto.PythonResponse> response = restTemplate.postForEntity(
                    chatbotServerUrl + "/chat",
                    payload,
                    ChatbotDto.PythonResponse.class
            );
            if (response.getBody() != null && response.getBody().getAnswer() != null) {
                return response.getBody().getAnswer();
            }
            return "죄송합니다. 응답을 받지 못했습니다.";
        } catch (Exception e) {
            log.error("[Chatbot] Python 서버 오류: {}", e.getMessage());
            return "현재 챗봇 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    /** 내 세션 목록 */
    public List<ChatbotDto.SessionResponse> getSessions(Long memberId) {
        return sessionRepository.findByMember_MemberIdOrderByUpdateTimeDesc(memberId)
                .stream()
                .map(s -> ChatbotDto.SessionResponse.builder()
                        .sessionId(s.getSessionId())
                        .insertTime(s.getInsertTime())
                        .updateTime(s.getUpdateTime())
                        .build())
                .collect(Collectors.toList());
    }

    /** 세션 대화 이력 조회 */
    public List<ChatbotDto.MessageResponse> getMessages(Long memberId, Long sessionId) {
        ChatbotSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> CommonException.notFound("세션을 찾을 수 없습니다."));

        if (!session.getMember().getMemberId().equals(memberId)) {
            throw CommonException.forbidden("본인의 대화만 조회할 수 있습니다.");
        }

        return messageRepository.findBySession_SessionIdOrderByInsertTimeAsc(sessionId)
                .stream()
                .map(m -> ChatbotDto.MessageResponse.builder()
                        .messageId(m.getMessageId())
                        .role(m.getRole())
                        .content(m.getContent())
                        .insertTime(m.getInsertTime())
                        .build())
                .collect(Collectors.toList());
    }
}
