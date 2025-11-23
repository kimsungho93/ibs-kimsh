package com.example.demo.chat.infra;

import com.example.demo.chat.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    /**
     * 특정 채팅방의 메시지 조회 (페이징, 최신순)
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.isDeleted = false ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findByRoomId(@Param("roomId") String roomId, Pageable pageable);

    /**
     * 특정 채팅방의 최근 N개 메시지 조회
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.isDeleted = false ORDER BY cm.createdAt DESC")
    List<ChatMessage> findRecentMessages(@Param("roomId") String roomId, Pageable pageable);

    /**
     * 특정 사용자가 보낸 메시지 개수
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.senderId = :senderId AND cm.isDeleted = false")
    Long countBySenderId(@Param("senderId") Long senderId);
}
