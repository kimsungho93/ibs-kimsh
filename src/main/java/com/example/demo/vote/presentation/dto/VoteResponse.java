package com.example.demo.vote.presentation.dto;

import com.example.demo.vote.domain.Vote;
import com.example.demo.vote.domain.VoteOption;
import com.example.demo.vote.domain.VoteStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
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
    private boolean allowAddOption;
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
                                    List<Long> myVotedOptionIds,
                                    String authorProfileImageUrl) {
        return VoteResponse.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .description(vote.getDescription())
                .author(AuthorResponse.from(vote, authorProfileImageUrl))
                .options(vote.getOptions().stream()
                        .sorted(Comparator.comparingInt(VoteOption::getDisplayOrder))
                        .map(option -> OptionResponse.from(
                                option,
                                voteCountMap.getOrDefault(option.getId(), 0L),
                                votersMap.getOrDefault(option.getId(), List.of())))
                        .toList())
                .isAnonymous(vote.isAnonymous())
                .isMultipleChoice(vote.isMultipleChoice())
                .allowAddOption(vote.isAllowAddOption())
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
        private int displayOrder;
        private long voteCount;
        private List<VoterResponse> voters;
        private Long addedByUserId;

        public static OptionResponse from(VoteOption option, long voteCount, List<VoterResponse> voters) {
            return OptionResponse.builder()
                    .id(option.getId())
                    .text(option.getText())
                    .displayOrder(option.getDisplayOrder())
                    .voteCount(voteCount)
                    .voters(voters)
                    .addedByUserId(option.getAddedByUserId())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class AuthorResponse {
        private Long id;
        private String name;
        private String profileImage;

        public static AuthorResponse from(Vote vote, String profileImageUrl) {
            return AuthorResponse.builder()
                    .id(vote.getAuthor().getId())
                    .name(vote.getAuthor().getName())
                    .profileImage(profileImageUrl)
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
