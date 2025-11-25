package com.example.demo.vote.presentation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateVoteRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 2, max = 100, message = "제목은 2~100자여야 합니다.")
    private String title;

    @Size(max = 500, message = "설명은 최대 500자입니다.")
    private String description;

    @NotNull(message = "마감일시는 필수입니다.")
    @Future(message = "마감일시는 현재 시간 이후여야 합니다.")
    private LocalDateTime endDate;
}
