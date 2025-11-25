package com.example.demo.vote.infra;

import com.example.demo.vote.domain.Vote;
import com.example.demo.vote.domain.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {

    List<VoteOption> findByVoteAndIdIn(Vote vote, List<Long> ids);
}
