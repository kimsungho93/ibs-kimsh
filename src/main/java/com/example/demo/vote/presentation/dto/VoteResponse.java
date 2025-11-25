package com.example.demo.vote.presentation.dto;

import com.example.demo.vote.domain.Vote;
import com.example.demo.vote.domain.VoteOption;
import com.example.demo.vote.domain.VoteStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class VoteResponse {

    private Long id;
    private String title;
    private String description;
    private AuthorResponse author;
    private List<OptionResponse> options;
    private boolean isAnonymous;
    private boolean isMultipleChoice;
    private int totalParticipants;
    private long totalActiveMembers;
    private LocalDateTime endDate;
    private VoteStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> myVotedOptionIds;

    public static VoteResponse from(Vote vote,
                                    Map<Long, Long> voteCountMap,
                                    Map<Long, List<VoterResponse>> votersMap,
                                    int totalParticipants,
                                    long totalActiveMembers,
                                    List<Long> myVotedOptionIds) {
        return VoteResponse.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .description(vote.getDescription())
                .author(AuthorResponse.from(vote))
                .options(vote.getOptions().stream()
                        .map(option -> OptionResponse.from(
                                option,
                                voteCountMap.getOrDefault(option.getId(), 0L),
                                votersMap.getOrDefault(option.getId(), List.of())))
                        .toList())
                .isAnonymous(vote.isAnonymous())
                .isMultipleChoice(vote.isMultipleChoice())
                .totalParticipants(totalParticipants)
                .totalActiveMembers(totalActiveMembers)
                .endDate(vote.getEndDate())
                .status(vote.getStatus())
                .createdAt(vote.getCreatedAt())
                .updatedAt(vote.getUpdatedAt())
                .myVotedOptionIds(myVotedOptionIds)
                .build();
    }

    @Getter
    @Builder
    public static class OptionResponse {
        private Long id;
        private String text;
        private long voteCount;
        private List<VoterResponse> voters;

        public static OptionResponse from(VoteOption option, long voteCount, List<VoterResponse> voters) {
            return OptionResponse.builder()
                    .id(option.getId())
                    .text(option.getText())
                    .voteCount(voteCount)
                    .voters(voters)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class AuthorResponse {
        private Long id;
        private String name;
        private String profileImage;

        public static AuthorResponse from(Vote vote) {
            return AuthorResponse.builder()
                    .id(vote.getAuthor().getId())
                    .name(vote.getAuthor().getName())
                    .profileImage(null)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class VoterResponse {
        private Long id;
        private String name;
        private String profileImage;
        private LocalDateTime votedAt;
    }
}
