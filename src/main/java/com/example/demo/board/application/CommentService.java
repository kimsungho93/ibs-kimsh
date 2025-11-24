package com.example.demo.board.application;

import com.example.demo.board.domain.Comment;
import com.example.demo.board.domain.Post;
import com.example.demo.board.infra.CommentRepository;
import com.example.demo.board.infra.PostRepository;
import com.example.demo.board.presentation.dto.CommentDto;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<CommentDto.Response> getComments(Long postId) {
        // Fetch all comments for the post
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        
        // Filter root comments (parentId is null) and map to DTO
        // Children are handled in DTO mapping or we can build tree here.
        // The DTO `from` method handles children mapping if the entity relationship is set up correctly.
        // However, since we are fetching all comments, we might need to rely on JPA relationship for children.
        // Ideally, we should fetch root comments and let JPA fetch children (N+1 issue possible if not batch fetched).
        // For simplicity and given the requirement, we'll assume the entity's `children` list is populated.
        // But `findByPostId` returns flat list. 
        // Let's rely on the fact that we can filter the flat list for roots.
        
        return comments.stream()
                .filter(c -> c.getParentId() == null)
                .map(CommentDto.Response::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto.Response createComment(Long postId, CommentDto.CreateRequest request, User author) {
        Post post = getPostById(postId);

        Comment comment = request.toEntity(post, author, request.getParentId());
        commentRepository.save(comment);
        post.increaseCommentCount();

        return CommentDto.Response.from(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = getCommentById(commentId);
        validateCommentOwnership(comment, user);

        commentRepository.delete(comment);
        comment.getPost().decreaseCommentCount();
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateCommentOwnership(Comment comment, User user) {
        if (user.getRole() != User.Role.ADMIN && !comment.getAuthor().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.COMMENT_FORBIDDEN);
        }
    }
}
