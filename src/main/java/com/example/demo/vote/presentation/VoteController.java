package com.example.demo.vote.presentation;

import com.example.demo.user.application.UserService;
import com.example.demo.user.domain.User;
import com.example.demo.vote.application.VoteService;
import com.example.demo.vote.domain.VoteStatus;
import com.example.demo.vote.presentation.dto.AddVoteOptionRequest;
import com.example.demo.vote.presentation.dto.CastVoteRequest;
import com.example.demo.vote.presentation.dto.CreateVoteRequest;
import com.example.demo.vote.presentation.dto.UpdateVoteRequest;
import com.example.demo.vote.presentation.dto.VoteListResponse;
import com.example.demo.vote.presentation.dto.VoteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createVote(
            @Valid @RequestBody CreateVoteRequest request,
            @AuthenticationPrincipal String email
    ) {
        User user = userService.findByEmail(email);
        VoteResponse response = voteService.createVote(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getVote(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        User user = userService.findByEmail(email);
        VoteResponse response = voteService.getVote(id, user);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getVotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) VoteStatus status,
            @AuthenticationPrincipal String email
    ) {
        User user = userService.findByEmail(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<VoteListResponse> votePage = voteService.getVotes(status, pageable, user);

        return ResponseEntity.ok(Map.of("success", true, "data", votePage));
    }

    @PostMapping("/{id}/cast")
    public ResponseEntity<Map<String, Object>> castVote(
            @PathVariable Long id,
            @Valid @RequestBody CastVoteRequest request,
            @AuthenticationPrincipal String email
    ) {
        User user = userService.findByEmail(email);
        VoteResponse response = voteService.castVote(id, request.getOptionIds(), user);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateVote(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVoteRequest request,
            @AuthenticationPrincipal String email
    ) {
        User user = userService.findByEmail(email);
        VoteResponse response = voteService.updateVote(id, request, user);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteVote(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        User user = userService.findByEmail(email);
        voteService.deleteVote(id, user);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of("deleted", true),
                "message", "투표가 삭제되었습니다."
        ));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<Map<String, Object>> closeVote(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        User user = userService.findByEmail(email);
        VoteResponse response = voteService.closeVote(id, user);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @PostMapping("/{voteId}/options")
    public ResponseEntity<Map<String, Object>> addOption(
            @PathVariable Long voteId,
            @Valid @RequestBody AddVoteOptionRequest request,
            @AuthenticationPrincipal String email
    ) {
        User user = userService.findByEmail(email);
        VoteResponse.OptionResponse response = voteService.addOption(voteId, request.getText(), user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", response));
    }
}
