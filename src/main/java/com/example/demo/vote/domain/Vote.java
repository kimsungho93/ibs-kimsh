package com.example.demo.vote.domain;

import com.example.demo.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "votes", indexes = {
        @Index(name = "idx_vote_status", columnList = "status"),
        @Index(name = "idx_vote_created_at", columnList = "createdAt DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "is_anonymous", nullable = false)
    private boolean anonymous;

    @Column(name = "is_multiple_choice", nullable = false)
    private boolean multipleChoice;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VoteStatus status = VoteStatus.ACTIVE;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VoteOption> options = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void addOption(VoteOption option) {
        this.options.add(option);
        option.setVote(this);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }

    public void close() {
        this.status = VoteStatus.CLOSED;
    }

    public void update(String title, String description, LocalDateTime endDate) {
        this.title = title;
        this.description = description;
        this.endDate = endDate;
    }

    public boolean isAuthor(User user) {
        return this.author.getId().equals(user.getId());
    }
}
