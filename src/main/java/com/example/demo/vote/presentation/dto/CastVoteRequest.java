package com.example.demo.vote.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CastVoteRequest {

    @NotNull(message = "선택지 목록은 필수입니다.")
    private List<Long> optionIds;
}
