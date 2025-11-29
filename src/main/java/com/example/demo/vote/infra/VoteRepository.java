package com.example.demo.vote.infra;

import com.example.demo.vote.domain.Vote;
import com.example.demo.vote.domain.VoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByStatusAndEndDateBefore(VoteStatus status, LocalDateTime dateTime);

    @Query("SELECT v FROM Vote v " +
            "JOIN FETCH v.author " +
            "LEFT JOIN FETCH v.options " +
            "WHERE v.id = :id")
    Optional<Vote> findByIdWithAuthorAndOptions(@Param("id") Long id);

    @Query("SELECT v FROM Vote v JOIN FETCH v.author LEFT JOIN FETCH v.options WHERE v.status = :status")
    Page<Vote> findByStatusWithAuthorAndOptions(@Param("status") VoteStatus status, Pageable pageable);

    @Query("SELECT v FROM Vote v JOIN FETCH v.author LEFT JOIN FETCH v.options")
    Page<Vote> findAllWithAuthorAndOptions(Pageable pageable);
}
