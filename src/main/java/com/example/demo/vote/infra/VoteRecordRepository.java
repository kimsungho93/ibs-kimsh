package com.example.demo.vote.infra;

import com.example.demo.user.domain.User;
import com.example.demo.vote.domain.Vote;
import com.example.demo.vote.domain.VoteOption;
import com.example.demo.vote.domain.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    boolean existsByVoteAndUser(Vote vote, User user);

    List<VoteRecord> findByVoteAndUser(Vote vote, User user);

    @Query("SELECT vr.option.id, COUNT(vr) FROM VoteRecord vr WHERE vr.vote = :vote GROUP BY vr.option.id")
    List<Object[]> countByVoteGroupByOption(@Param("vote") Vote vote);

    long countByOption(VoteOption option);

    @Query("SELECT vr FROM VoteRecord vr JOIN FETCH vr.user WHERE vr.vote = :vote")
    List<VoteRecord> findByVoteWithUser(@Param("vote") Vote vote);

    @Query("SELECT COUNT(DISTINCT vr.user.id) FROM VoteRecord vr WHERE vr.vote = :vote")
    int countDistinctUserByVote(@Param("vote") Vote vote);

    @Query("SELECT vr.vote.id, COUNT(DISTINCT vr.user.id) FROM VoteRecord vr WHERE vr.vote IN :votes GROUP BY vr.vote.id")
    List<Object[]> countDistinctUserByVotes(@Param("votes") List<Vote> votes);

    @Query("SELECT vr.vote.id FROM VoteRecord vr WHERE vr.vote IN :votes AND vr.user = :user")
    List<Long> findVotedVoteIdsByVotesAndUser(@Param("votes") List<Vote> votes, @Param("user") User user);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM VoteRecord vr WHERE vr.vote = :vote AND vr.user = :user")
    void deleteByVoteAndUser(@Param("vote") Vote vote, @Param("user") User user);

    @Modifying
    @Query("DELETE FROM VoteRecord vr WHERE vr.vote = :vote")
    void deleteByVote(@Param("vote") Vote vote);
}
