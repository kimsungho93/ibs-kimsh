package com.example.demo.board.presentation;

import com.example.demo.board.application.PostService;
import com.example.demo.board.domain.Post;
import com.example.demo.board.presentation.dto.PostDto;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.user.domain.User;
import com.example.demo.user.infra.UserRepository;
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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(required = false) Post.Category category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("popular".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "likeCount", "viewCount");
        }

        Pageable pageable = PageRequest.of(page - 1, limit, sortObj);
        Page<PostDto.SummaryResponse> posts = postService.getPosts(category, pageable);

        return ResponseEntity.ok(Map.of(
                "data", posts.getContent(),
                "meta", Map.of(
                        "totalCount", posts.getTotalElements(),
                        "currentPage", page,
                        "totalPages", posts.getTotalPages()
                )
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto.Response> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        User user = null;
        if (email != null) {
            user = userRepository.findByEmail(email).orElse(null);
        }
        return ResponseEntity.ok(postService.getPost(id, user));
    }

    @PostMapping
    public ResponseEntity<PostDto.Response> createPost(
            @RequestBody PostDto.CreateRequest request,
            @AuthenticationPrincipal String email
    ) {
        User user = getUserByEmail(email);
        PostDto.Response response = postService.createPost(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto.Response> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto.UpdateRequest request,
            @AuthenticationPrincipal String email
    ) {
        User user = getUserByEmail(email);
        return ResponseEntity.ok(postService.updatePost(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        User user = getUserByEmail(email);
        postService.deletePost(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        User user = getUserByEmail(email);
        return ResponseEntity.ok(postService.toggleLike(id, user));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
