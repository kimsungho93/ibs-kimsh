package com.example.demo.vote.application;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.user.application.UserService;
import com.example.demo.user.domain.User;
import com.example.demo.vote.domain.Vote;
import com.example.demo.vote.domain.VoteOption;
import com.example.demo.vote.domain.VoteRecord;
import com.example.demo.vote.infra.VoteOptionRepository;
import com.example.demo.vote.infra.VoteRecordRepository;
import com.example.demo.vote.infra.VoteRepository;
import com.example.demo.vote.domain.VoteStatus;
import com.example.demo.user.domain.User.Role;
import com.example.demo.vote.presentation.dto.CreateVoteRequest;
import com.example.demo.vote.presentation.dto.UpdateVoteOptionRequest;
import com.example.demo.vote.presentation.dto.UpdateVoteRequest;
import com.example.demo.vote.presentation.dto.VoteListResponse;
import com.example.demo.vote.presentation.dto.VoteResponse;
import com.example.demo.vote.presentation.dto.VoteResponse.VoterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {

    private static final int MIN_OPTION_COUNT = 2;
    private static final int MAX_OPTION_COUNT = 10;

    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final UserService userService;

    @Transactional
    public VoteResponse createVote(CreateVoteRequest request, User author) {
        Vote vote = buildVote(request, author);
        addOptionsToVote(vote, request.getOptions());

        Vote savedVote = voteRepository.save(vote);
        long totalActiveMembers = userService.countActiveUsers();

        return VoteResponse.from(
                savedVote,
                Collections.emptyMap(),
                Collections.emptyMap(),
                0,
                totalActiveMembers,
                Collections.emptyList()
        );
    }

    private Vote buildVote(CreateVoteRequest request, User author) {
        return Vote.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .author(author)
                .anonymous(request.getIsAnonymous())
                .multipleChoice(request.getIsMultipleChoice())
                .allowAddOption(request.getAllowAddOption())
                .endDate(request.getEndDate())
                .build();
    }

    private void addOptionsToVote(Vote vote, List<String> optionTexts) {
        for (int i = 0; i < optionTexts.size(); i++) {
            VoteOption option = VoteOption.builder()
                    .text(optionTexts.get(i))
                    .displayOrder(i)
                    .build();
            vote.addOption(option);
        }
    }

    public VoteResponse getVote(Long voteId, User currentUser) {
        Vote vote = findVoteOrThrow(voteId);

        Map<Long, Long> voteCountMap = buildVoteCountMap(vote);
        Map<Long, List<VoterResponse>> votersMap = buildVotersMap(vote);
        int totalParticipants = voteRecordRepository.countDistinctUserByVote(vote);
        long totalActiveMembers = userService.countActiveUsers();
        List<Long> myVotedOptionIds = findMyVotedOptionIds(vote, currentUser);

        return VoteResponse.from(
                vote,
                voteCountMap,
                votersMap,
                totalParticipants,
                totalActiveMembers,
                myVotedOptionIds
        );
    }

    private Vote findVoteOrThrow(Long voteId) {
        return voteRepository.findByIdWithAuthorAndOptions(voteId)
                .orElseThrow(() -> new CustomException(ErrorCode.VOTE_NOT_FOUND));
    }

    private Map<Long, Long> buildVoteCountMap(Vote vote) {
        return voteRecordRepository.countByVoteGroupByOption(vote).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    private Map<Long, List<VoterResponse>> buildVotersMap(Vote vote) {
        if (vote.isAnonymous()) {
            return Collections.emptyMap();
        }

        List<VoteRecord> records = voteRecordRepository.findByVoteWithUser(vote);
        return records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getOption().getId(),
                        Collectors.mapping(this::toVoterResponse, Collectors.toList())
                ));
    }

    private VoterResponse toVoterResponse(VoteRecord record) {
        User user = record.getUser();
        return VoterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImage(null)
                .votedAt(record.getVotedAt())
                .build();
    }

    private List<Long> findMyVotedOptionIds(Vote vote, User currentUser) {
        return voteRecordRepository.findByVoteAndUser(vote, currentUser).stream()
                .map(record -> record.getOption().getId())
                .toList();
    }

    public Page<VoteListResponse> getVotes(VoteStatus status, Pageable pageable, User currentUser) {
        Page<Vote> votePage = findVotesByStatus(status, pageable);
        List<Vote> votes = votePage.getContent();

        if (votes.isEmpty()) {
            return votePage.map(vote -> null);
        }

        Map<Long, Integer> participantCountMap = buildParticipantCountMap(votes);
        Set<Long> votedVoteIds = findVotedVoteIds(votes, currentUser);
        long totalActiveMembers = userService.countActiveUsers();

        return votePage.map(vote -> VoteListResponse.from(
                vote,
                participantCountMap.getOrDefault(vote.getId(), 0),
                totalActiveMembers,
                votedVoteIds.contains(vote.getId())
        ));
    }

    private Page<Vote> findVotesByStatus(VoteStatus status, Pageable pageable) {
        if (status == null) {
            return voteRepository.findAllWithAuthorAndOptions(pageable);
        }
        return voteRepository.findByStatusWithAuthorAndOptions(status, pageable);
    }

    private Map<Long, Integer> buildParticipantCountMap(List<Vote> votes) {
        return voteRecordRepository.countDistinctUserByVotes(votes).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
    }

    private Set<Long> findVotedVoteIds(List<Vote> votes, User currentUser) {
        return new HashSet<>(voteRecordRepository.findVotedVoteIdsByVotesAndUser(votes, currentUser));
    }

    @Transactional
    public VoteResponse castVote(Long voteId, List<Long> optionIds, User currentUser) {
        Vote vote = findVoteOrThrow(voteId);

        validateVoteIsActive(vote);
        validateOptionCount(vote, optionIds);

        voteRecordRepository.deleteByVoteAndUser(vote, currentUser);

        if (!optionIds.isEmpty()) {
            List<VoteOption> options = findOptionsOrThrow(vote, optionIds);
            saveVoteRecords(vote, options, currentUser);
        }

        return getVote(voteId, currentUser);
    }

    private void validateVoteIsActive(Vote vote) {
        if (vote.isClosed()) {
            throw new CustomException(ErrorCode.VOTE_CLOSED);
        }
        if (vote.isExpired()) {
            throw new CustomException(ErrorCode.VOTE_EXPIRED);
        }
    }

    private void validateOptionCount(Vote vote, List<Long> optionIds) {
        if (!vote.isMultipleChoice() && optionIds.size() > 1) {
            throw new CustomException(ErrorCode.VOTE_SINGLE_CHOICE_ONLY);
        }
    }

    private List<VoteOption> findOptionsOrThrow(Vote vote, List<Long> optionIds) {
        List<VoteOption> options = voteOptionRepository.findByVoteAndIdIn(vote, optionIds);
        if (options.size() != optionIds.size()) {
            throw new CustomException(ErrorCode.VOTE_OPTION_NOT_FOUND);
        }
        return options;
    }

    private void saveVoteRecords(Vote vote, List<VoteOption> options, User user) {
        List<VoteRecord> records = options.stream()
                .map(option -> VoteRecord.builder()
                        .vote(vote)
                        .option(option)
                        .user(user)
                        .build())
                .toList();
        voteRecordRepository.saveAll(records);
    }

    @Transactional
    public VoteResponse updateVote(Long voteId, UpdateVoteRequest request, User currentUser) {
        Vote vote = findVoteOrThrow(voteId);

        validateAuthorOrAdmin(vote, currentUser);
        validateVoteNotClosed(vote);

        vote.update(request.getTitle(), request.getDescription(), request.getEndDate());

        if (request.getOptions() != null) {
            updateVoteOptions(vote, request.getOptions(), request.getDeletedOptionIds());
        }

        return getVote(voteId, currentUser);
    }

    private void validateVoteNotClosed(Vote vote) {
        if (vote.isClosed()) {
            throw new CustomException(ErrorCode.VOTE_CLOSED);
        }
    }

    private void updateVoteOptions(Vote vote,
                                   List<UpdateVoteOptionRequest> optionRequests,
                                   List<Long> deletedOptionIds) {
        deleteOptions(vote, deletedOptionIds);
        applyOptionUpdates(vote, optionRequests);
        validateOptionCount(vote.getOptions().size());
        validateNoDuplicateOptions(optionRequests);
    }

    private void deleteOptions(Vote vote, List<Long> deletedOptionIds) {
        if (deletedOptionIds == null || deletedOptionIds.isEmpty()) {
            return;
        }

        List<VoteOption> optionsToDelete = vote.getOptions().stream()
                .filter(option -> deletedOptionIds.contains(option.getId()))
                .toList();

        validateNoVotesOnOptions(optionsToDelete);

        vote.getOptions().removeAll(optionsToDelete);
        voteOptionRepository.deleteAllById(deletedOptionIds);
    }

    private void validateNoVotesOnOptions(List<VoteOption> options) {
        boolean hasVotes = options.stream()
                .anyMatch(voteRecordRepository::existsByOption);

        if (hasVotes) {
            throw new CustomException(ErrorCode.VOTE_OPTION_HAS_VOTES);
        }
    }

    private void applyOptionUpdates(Vote vote, List<UpdateVoteOptionRequest> optionRequests) {
        Map<Long, VoteOption> existingOptions = vote.getOptions().stream()
                .collect(Collectors.toMap(VoteOption::getId, option -> option));

        for (UpdateVoteOptionRequest request : optionRequests) {
            if (request.getId() != null) {
                updateExistingOption(existingOptions, request);
            } else {
                addNewOption(vote, request);
            }
        }
    }

    private void updateExistingOption(Map<Long, VoteOption> existingOptions,
                                      UpdateVoteOptionRequest request) {
        VoteOption option = existingOptions.get(request.getId());
        if (option == null) {
            throw new CustomException(ErrorCode.VOTE_OPTION_NOT_FOUND);
        }
        option.update(request.getText(), request.getDisplayOrder());
    }

    private void addNewOption(Vote vote, UpdateVoteOptionRequest request) {
        VoteOption newOption = VoteOption.builder()
                .text(request.getText())
                .displayOrder(request.getDisplayOrder())
                .build();
        vote.addOption(newOption);
    }

    private void validateOptionCount(int optionCount) {
        if (optionCount < MIN_OPTION_COUNT) {
            throw new CustomException(ErrorCode.VOTE_OPTION_MIN_COUNT);
        }
        if (optionCount > MAX_OPTION_COUNT) {
            throw new CustomException(ErrorCode.VOTE_OPTION_MAX_COUNT);
        }
    }

    private void validateNoDuplicateOptions(List<UpdateVoteOptionRequest> optionRequests) {
        Set<String> uniqueTexts = optionRequests.stream()
                .map(request -> request.getText().trim().toLowerCase())
                .collect(Collectors.toSet());

        if (uniqueTexts.size() != optionRequests.size()) {
            throw new CustomException(ErrorCode.VOTE_OPTION_DUPLICATE);
        }
    }

    @Transactional
    public void deleteVote(Long voteId, User currentUser) {
        Vote vote = findVoteOrThrow(voteId);

        validateAuthorOrAdmin(vote, currentUser);

        voteRecordRepository.deleteByVote(vote);
        voteRepository.delete(vote);
    }

    @Transactional
    public VoteResponse closeVote(Long voteId, User currentUser) {
        Vote vote = findVoteOrThrow(voteId);

        validateAuthorOrAdmin(vote, currentUser);

        vote.close();

        return getVote(voteId, currentUser);
    }

    private void validateAuthorOrAdmin(Vote vote, User user) {
        boolean isAuthor = vote.isAuthor(user);
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    @Transactional
    public VoteResponse.OptionResponse addOption(Long voteId, String text, User currentUser) {
        Vote vote = findVoteOrThrow(voteId);

        validateCanAddOption(vote);
        validateNoDuplicateOptionText(vote, text);
        validateOptionCount(vote.getOptions().size() + 1);

        int nextDisplayOrder = getNextDisplayOrder(vote);
        VoteOption newOption = VoteOption.builder()
                .text(text.trim())
                .displayOrder(nextDisplayOrder)
                .addedByUserId(currentUser.getId())
                .build();
        vote.addOption(newOption);

        voteRepository.flush();

        return VoteResponse.OptionResponse.from(newOption, 0, List.of());
    }

    private void validateCanAddOption(Vote vote) {
        if (!vote.isAllowAddOption()) {
            throw new CustomException(ErrorCode.VOTE_ADD_OPTION_NOT_ALLOWED);
        }
        validateVoteIsActive(vote);
    }

    private void validateNoDuplicateOptionText(Vote vote, String text) {
        boolean exists = vote.getOptions().stream()
                .anyMatch(option -> option.getText().trim().equalsIgnoreCase(text.trim()));

        if (exists) {
            throw new CustomException(ErrorCode.VOTE_OPTION_DUPLICATE);
        }
    }

    private int getNextDisplayOrder(Vote vote) {
        return vote.getOptions().stream()
                .mapToInt(VoteOption::getDisplayOrder)
                .max()
                .orElse(-1) + 1;
    }
}
