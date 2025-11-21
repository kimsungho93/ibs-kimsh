package com.example.demo.suggestion.domain;

import com.example.demo.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 건의사항 엔티티
 */
@Entity
@Table(name = "suggestions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suggestion_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryType category;

    @Column(nullable = false, length = 50)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "suggestion_attachments",
            joinColumns = @JoinColumn(name = "suggestion_id")
    )
    @Builder.Default
    private List<FileMetadata> attachments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusType status = StatusType.PENDING;

    @Column(length = 500)
    private String adminComment;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 상태 및 관리자 답변 업데이트
     */
    public void updateStatusAndComment(StatusType status, String adminComment) {
        this.status = status;
        this.adminComment = adminComment;
    }
}
