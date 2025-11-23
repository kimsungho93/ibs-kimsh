package com.example.demo.chat.application;

import com.example.demo.chat.domain.ChatMessage;
import com.example.demo.chat.domain.ChatRoom;
import com.example.demo.chat.domain.MessageType;
import com.example.demo.chat.infra.ChatMessageRepository;
import com.example.demo.chat.infra.ChatRoomRepository;
import com.example.demo.chat.presentation.dto.ChatMessageResponse;
import com.example.demo.chat.presentation.dto.ChatRoomCreateRequest;
import com.example.demo.chat.presentation.dto.ChatRoomResponse;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.user.domain.User;
import com.example.demo.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 채팅방 목록 조회
     */
    public List<ChatRoomResponse> getRoomList() {
        return chatRoomRepository.findAllActiveRooms().stream()
                .map(ChatRoomResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 생성
     */
    @Transactional
    public ChatRoomResponse createRoom(ChatRoomCreateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String encodedPassword = null;
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            encodedPassword = passwordEncoder.encode(request.getPassword());
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getName())
                .password(encodedPassword)
                .creatorId(user.getId())
                .creatorName(user.getName())
                .maxParticipants(request.getMaxParticipants())
                .currentParticipants(0)
                .build();

        chatRoomRepository.save(chatRoom);
        return ChatRoomResponse.from(chatRoom);
    }

    /**
     * 채팅방 입장 (비밀번호 검증)
     */
    @Transactional
    public void joinRoom(String roomId, String password, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findActiveById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (chatRoom.isFull()) {
            throw new CustomException(ErrorCode.CHAT_ROOM_FULL);
        }

        if (chatRoom.hasPassword()) {
            if (password == null || !passwordEncoder.matches(password, chatRoom.getPassword())) {
                throw new CustomException(ErrorCode.CHAT_ROOM_PASSWORD_MISMATCH);
            }
        }

        chatRoom.addParticipant(user.getId());
    }

    /**
     * 채팅방 퇴장
     */
    @Transactional
    public void leaveRoom(String roomId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findActiveById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        chatRoom.removeParticipant(user.getId());

        // 빈 채팅방 자동 삭제
        if (chatRoom.getCurrentParticipants() == 0) {
            chatRoom.delete();
        }
    }

    /**
     * 메시지 조회 (페이징)
     */
    public Page<ChatMessageResponse> getMessages(String roomId, Pageable pageable) {
        return chatMessageRepository.findByRoomId(roomId, pageable)
                .map(ChatMessageResponse::from);
    }

    /**
     * 메시지 저장
     */
    @Transactional
    public ChatMessage saveMessage(String roomId, Long senderId, String senderName, String content, MessageType type) {
        ChatRoom chatRoom = chatRoomRepository.findActiveById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(senderId)
                .senderName(senderName)
                .content(content)
                .type(type)
                .build();

        return chatMessageRepository.save(message);
    }

    /**
     * 채팅방 조회
     */
    public ChatRoomResponse getRoom(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findActiveById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return ChatRoomResponse.from(chatRoom);
    }
}
