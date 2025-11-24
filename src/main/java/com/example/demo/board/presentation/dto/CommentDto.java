package com.example.demo.board.presentation.dto;

import com.example.demo.board.domain.Comment;
import com.example.demo.board.domain.Post;
import com.example.demo.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDto {

    @Getter
    @Builder
    public static class CreateRequest {
        private String content;
        private Long parentId;

        public Comment toEntity(Post post, User author, Long parentId) {
            return Comment.builder()
                    .post(post)
                    .content(content)
                    .author(author)
                    .parentId(parentId)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String content;
        private PostDto.AuthorResponse author;
        private Long parentId;
        private boolean isDeleted;
        private LocalDateTime createdAt;
        private List<Response> children;

        public static Response from(Comment comment) {
            List<Response> children = comment.getChildren() != null
                    ? comment.getChildren().stream()
                            .map(Response::from)
                            .collect(Collectors.toList())
                    : List.of();

            return Response.builder()
                    .id(comment.getId())
                    .content(comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent())
                    .author(PostDto.AuthorResponse.from(comment.getAuthor()))
                    .parentId(comment.getParentId())
                    .isDeleted(comment.isDeleted())
                    .createdAt(comment.getCreatedAt())
                    .children(children)
                    .build();
        }
    }
}
