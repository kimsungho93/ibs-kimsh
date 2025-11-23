package com.example.demo.chat.presentation.dto;

import com.example.demo.chat.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponse {
    private String id;
    private String name;
    private boolean hasPassword;
    private Long creatorId;
    private String creatorName;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private LocalDateTime createdAt;

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .hasPassword(chatRoom.hasPassword())
                .creatorId(chatRoom.getCreatorId())
                .creatorName(chatRoom.getCreatorName())
                .maxParticipants(chatRoom.getMaxParticipants())
                .currentParticipants(chatRoom.getCurrentParticipants())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}
