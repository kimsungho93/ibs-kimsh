package com.example.demo.chat.infra;

import com.example.demo.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    /**
     * 삭제되지 않은 채팅방 목록 조회 (최신순)
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.isDeleted = false ORDER BY cr.createdAt DESC")
    List<ChatRoom> findAllActiveRooms();

    /**
     * 삭제되지 않은 채팅방 조회
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.id = :id AND cr.isDeleted = false")
    Optional<ChatRoom> findActiveById(@Param("id") String id);

    /**
     * 특정 사용자가 생성한 채팅방 목록
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.creatorId = :creatorId AND cr.isDeleted = false ORDER BY cr.createdAt DESC")
    List<ChatRoom> findByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 빈 채팅방 목록 조회 (자동 삭제 대상)
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.currentParticipants = 0 AND cr.isDeleted = false")
    List<ChatRoom> findEmptyRooms();
}
