package com.example.demo.vote.presentation.dto;

import com.example.demo.user.domain.User;
import com.example.demo.vote.domain.Vote;
import com.example.demo.vote.domain.VoteStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VoteListResponse {

    private Long id;
    private String title;
    private AuthorResponse author;
    private boolean isAnonymous;
    private boolean isMultipleChoice;
    private int totalParticipants;
    private long totalActiveMembers;
    private int optionCount;
    private LocalDateTime endDate;
    private VoteStatus status;
    private LocalDateTime createdAt;
    private boolean hasVoted;

    public static VoteListResponse from(Vote vote,
                                        int totalParticipants,
                                        long totalActiveMembers,
                                        boolean hasVoted) {
        return VoteListResponse.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .author(AuthorResponse.from(vote.getAuthor()))
                .isAnonymous(vote.isAnonymous())
                .isMultipleChoice(vote.isMultipleChoice())
                .totalParticipants(totalParticipants)
                .totalActiveMembers(totalActiveMembers)
                .optionCount(vote.getOptions().size())
                .endDate(vote.getEndDate())
                .status(vote.getStatus())
                .createdAt(vote.getCreatedAt())
                .hasVoted(hasVoted)
                .build();
    }

    @Getter
    @Builder
    public static class AuthorResponse {
        private Long id;
        private String name;
        private String profileImage;

        public static AuthorResponse from(User user) {
            return AuthorResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .profileImage(null)
                    .build();
        }
    }
}
