package com.example.demo.vote.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateVoteOptionRequest {

    private Long id;

    @NotBlank(message = "선택지 내용은 필수입니다.")
    @Size(max = 100, message = "선택지는 최대 100자입니다.")
    private String text;

    @NotNull(message = "순서는 필수입니다.")
    private Integer displayOrder;
}
