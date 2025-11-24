package com.example.demo.board.application;

import com.example.demo.board.domain.Post;
import com.example.demo.board.domain.PostLike;
import com.example.demo.board.infra.PostLikeRepository;
import com.example.demo.board.infra.PostRepository;
import com.example.demo.board.presentation.dto.PostDto;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public Page<PostDto.SummaryResponse> getPosts(Post.Category category, Pageable pageable) {
        Page<Post> posts;
        if (category != null) {
            posts = postRepository.findByCategory(category, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }
        return posts.map(PostDto.SummaryResponse::from);
    }

    @Transactional
    public PostDto.Response getPost(Long id, User user) {
        Post post = getPostById(id);
        post.increaseViewCount();
        
        boolean isLiked = false;
        if (user != null) {
            isLiked = postLikeRepository.existsByPostAndUser(post, user);
        }

        return PostDto.Response.from(post, isLiked);
    }

    @Transactional
    public PostDto.Response createPost(PostDto.CreateRequest request, User author) {
        validateNoticePermission(request.getCategory(), author);

        Post post = request.toEntity(author);
        postRepository.save(post);
        return PostDto.Response.from(post, false);
    }

    @Transactional
    public PostDto.Response updatePost(Long id, PostDto.UpdateRequest request, User user) {
        Post post = getPostById(id);
        validateOwnership(post, user);
        validateNoticePermission(request.getCategory(), user);

        post.update(request.getTitle(), request.getContent(), request.getCategory());
        
        boolean isLiked = postLikeRepository.existsByPostAndUser(post, user);
        return PostDto.Response.from(post, isLiked);
    }

    @Transactional
    public void deletePost(Long id, User user) {
        Post post = getPostById(id);
        validateOwnership(post, user);
        postRepository.delete(post);
    }

    @Transactional
    public Map<String, Object> toggleLike(Long id, User user) {
        Post post = getPostById(id);

        boolean liked = postLikeRepository.findByPostAndUser(post, user)
                .map(like -> {
                    postLikeRepository.delete(like);
                    post.decreaseLikeCount();
                    return false;
                })
                .orElseGet(() -> {
                    postLikeRepository.save(PostLike.builder().post(post).user(user).build());
                    post.increaseLikeCount();
                    return true;
                });

        return Map.of("liked", liked, "totalLikes", post.getLikeCount());
    }

    private Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private void validateOwnership(Post post, User user) {
        if (user.getRole() != User.Role.ADMIN && !post.getAuthor().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.POST_FORBIDDEN);
        }
    }

    private void validateNoticePermission(Post.Category category, User user) {
        if (category == Post.Category.NOTICE && user.getRole() != User.Role.ADMIN) {
            throw new CustomException(ErrorCode.NOTICE_POST_ONLY_ADMIN);
        }
    }
}
