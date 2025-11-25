package com.example.demo.vote.domain;

import com.example.demo.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vote_records",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vote_option_user", columnNames = {"vote_id", "option_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_record_vote_id", columnList = "vote_id"),
                @Index(name = "idx_record_user_id", columnList = "user_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VoteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private VoteOption option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime votedAt;
}
