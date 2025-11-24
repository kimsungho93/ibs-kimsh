package com.example.demo.board.presentation;

import com.example.demo.board.application.CommentService;
import com.example.demo.board.presentation.dto.CommentDto;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.user.domain.User;
import com.example.demo.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto.Response>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentDto.Response> createComment(
            @PathVariable Long postId,
            @RequestBody CommentDto.CreateRequest request,
            @AuthenticationPrincipal String email
    ) {
        User user = getUserByEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(postId, request, user));
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        User user = getUserByEmail(email);
        commentService.deleteComment(id, user);
        return ResponseEntity.noContent().build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
