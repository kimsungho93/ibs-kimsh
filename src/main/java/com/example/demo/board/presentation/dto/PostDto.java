package com.example.demo.board.presentation.dto;

import com.example.demo.board.domain.Post;
import com.example.demo.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class PostDto {

    @Getter
    @Builder
    public static class CreateRequest {
        private Post.Category category;
        private String title;
        private String content;

        public Post toEntity(User author) {
            return Post.builder()
                    .category(category)
                    .title(title)
                    .content(content)
                    .author(author)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class UpdateRequest {
        private Post.Category category;
        private String title;
        private String content;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Post.Category category;
        private String title;
        private String content;
        private AuthorResponse author;
        private int viewCount;
        private int likeCount;
        private int commentCount;
        private boolean isLiked;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Post post, boolean isLiked, String authorProfileImageUrl) {
            return Response.builder()
                    .id(post.getId())
                    .category(post.getCategory())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .author(AuthorResponse.from(post.getAuthor(), authorProfileImageUrl))
                    .viewCount(post.getViewCount())
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .isLiked(isLiked)
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SummaryResponse {
        private Long id;
        private Post.Category category;
        private String title;
        private AuthorResponse author;
        private int viewCount;
        private int likeCount;
        private int commentCount;
        private LocalDateTime createdAt;

        public static SummaryResponse from(Post post, String authorProfileImageUrl) {
            return SummaryResponse.builder()
                    .id(post.getId())
                    .category(post.getCategory())
                    .title(post.getTitle())
                    .author(AuthorResponse.from(post.getAuthor(), authorProfileImageUrl))
                    .viewCount(post.getViewCount())
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .createdAt(post.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class AuthorResponse {
        private Long id;
        private String nickname;
        private String profileImageUrl;

        public static AuthorResponse from(User user, String profileImageUrl) {
            return AuthorResponse.builder()
                    .id(user.getId())
                    .nickname(user.getName())
                    .profileImageUrl(profileImageUrl)
                    .build();
        }

        public static AuthorResponse from(User user) {
            return from(user, null);
        }
    }
}
