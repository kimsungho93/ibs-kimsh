package com.example.demo.vote.application;

import com.example.demo.vote.domain.Vote;
import com.example.demo.vote.domain.VoteStatus;
import com.example.demo.vote.infra.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoteScheduler {

    private final VoteRepository voteRepository;

    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void closeExpiredVotes() {
        LocalDateTime now = LocalDateTime.now();
        List<Vote> expiredVotes = voteRepository.findByStatusAndEndDateBefore(VoteStatus.ACTIVE, now);

        if (expiredVotes.isEmpty()) {
            return;
        }

        expiredVotes.forEach(Vote::close);
        log.info("만료된 투표 종료 - count: {}", expiredVotes.size());
    }
}
