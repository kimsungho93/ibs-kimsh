package com.example.demo.vote.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddVoteOptionRequest {

    @NotBlank(message = "선택지 내용은 필수입니다.")
    @Size(min = 1, max = 100, message = "선택지는 1~100자여야 합니다.")
    private String text;
}
