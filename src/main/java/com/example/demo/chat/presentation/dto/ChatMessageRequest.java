package com.example.demo.chat.presentation.dto;

import com.example.demo.chat.domain.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private String roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private MessageType type;
}
