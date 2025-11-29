package com.example.demo.vote.infra;

import com.example.demo.vote.domain.Vote;
import com.example.demo.vote.domain.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {

    List<VoteOption> findByVoteAndIdIn(Vote vote, List<Long> ids);

    @Query("SELECT MAX(vo.displayOrder) FROM VoteOption vo WHERE vo.vote = :vote")
    Optional<Integer> findMaxDisplayOrderByVote(@Param("vote") Vote vote);

    boolean existsByVoteAndTextIgnoreCase(Vote vote, String text);
}
