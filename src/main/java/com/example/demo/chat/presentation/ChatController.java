package com.example.demo.chat.presentation;

import com.example.demo.chat.application.ChatService;
import com.example.demo.chat.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 채팅방 목록 조회
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getRoomList() {
        List<ChatRoomResponse> rooms = chatService.getRoomList();
        return ResponseEntity.ok(rooms);
    }

    /**
     * 채팅방 생성
     */
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createRoom(
            @Valid @RequestBody ChatRoomCreateRequest request,
            @AuthenticationPrincipal String email
    ) {
        ChatRoomResponse room = chatService.createRoom(request, email);
        return ResponseEntity.ok(room);
    }

    /**
     * 채팅방 입장 (비밀번호 검증)
     */
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<Void> joinRoom(
            @PathVariable String roomId,
            @RequestBody(required = false) ChatRoomJoinRequest request,
            @AuthenticationPrincipal String email
    ) {

        String password = request != null ? request.getPassword() : null;
        chatService.joinRoom(roomId, password, email);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방 퇴장
     */
    @PostMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(
            @PathVariable String roomId,
            @AuthenticationPrincipal String email
    ) {
        chatService.leaveRoom(roomId, email);
        return ResponseEntity.ok().build();
    }

    /**
     * 메시지 조회 (페이징)
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getMessages(
            @PathVariable String roomId,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<ChatMessageResponse> messages = chatService.getMessages(roomId, pageable);
        return ResponseEntity.ok(messages);
    }
}
