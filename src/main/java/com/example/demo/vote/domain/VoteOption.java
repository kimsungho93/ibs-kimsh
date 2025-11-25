package com.example.demo.vote.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vote_options", indexes = {
        @Index(name = "idx_option_vote_id", columnList = "vote_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VoteOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @Column(nullable = false, length = 100)
    private String text;

    @Column(nullable = false)
    @Builder.Default
    private int displayOrder = 0;

    void setVote(Vote vote) {
        this.vote = vote;
    }
}
