package com.example.demo.chat.presentation;

import com.example.demo.chat.application.ChatService;
import com.example.demo.chat.domain.ChatMessage;
import com.example.demo.chat.domain.MessageType;
import com.example.demo.chat.presentation.dto.ChatMessageRequest;
import com.example.demo.chat.presentation.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket 채팅 메시지 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    /**
     * 메시지 전송
     * 클라이언트: /app/chat.send
     */
    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request) {
        // 메시지 저장
        ChatMessage savedMessage = chatService.saveMessage(
                request.getRoomId(),
                request.getSenderId(),
                request.getSenderName(),
                request.getContent(),
                MessageType.TEXT
        );

        // 해당 채팅방을 구독한 모든 클라이언트에게 메시지 전송
        ChatMessageResponse response = ChatMessageResponse.from(savedMessage);
        messagingTemplate.convertAndSend("/topic/chat/" + request.getRoomId(), response);
    }

    /**
     * 입장 알림
     * 클라이언트: /app/chat.enter
     */
    @MessageMapping("/chat.enter")
    public void enterUser(ChatMessageRequest request) {
        String content = request.getSenderName() + "님이 입장하셨습니다.";

        // 입장 메시지 저장
        ChatMessage savedMessage = chatService.saveMessage(
                request.getRoomId(),
                request.getSenderId(),
                request.getSenderName(),
                content,
                MessageType.ENTER
        );

        // 해당 채팅방을 구독한 모든 클라이언트에게 입장 알림 전송
        ChatMessageResponse response = ChatMessageResponse.from(savedMessage);
        messagingTemplate.convertAndSend("/topic/chat/" + request.getRoomId(), response);
    }

    /**
     * 퇴장 알림
     * 클라이언트: /app/chat.leave
     */
    @MessageMapping("/chat.leave")
    public void leaveUser(ChatMessageRequest request) {
        String content = request.getSenderName() + "님이 퇴장하셨습니다.";

        // 퇴장 메시지 저장
        ChatMessage savedMessage = chatService.saveMessage(
                request.getRoomId(),
                request.getSenderId(),
                request.getSenderName(),
                content,
                MessageType.LEAVE
        );

        // 해당 채팅방을 구독한 모든 클라이언트에게 퇴장 알림 전송
        ChatMessageResponse response = ChatMessageResponse.from(savedMessage);
        messagingTemplate.convertAndSend("/topic/chat/" + request.getRoomId(), response);
    }
}
