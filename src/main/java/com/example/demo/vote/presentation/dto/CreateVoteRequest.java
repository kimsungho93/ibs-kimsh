package com.example.demo.vote.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CreateVoteRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하로 입력해주세요.")
    private String title;

    @Size(max = 500, message = "설명은 500자 이하로 입력해주세요.")
    private String description;

    @NotEmpty(message = "선택지는 최소 1개 이상이어야 합니다.")
    private List<String> options;

    @NotNull(message = "익명 여부는 필수입니다.")
    private Boolean isAnonymous;

    @NotNull(message = "복수 선택 여부는 필수입니다.")
    private Boolean isMultipleChoice;

    private Boolean allowAddOption;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDateTime endDate;

    public Boolean getAllowAddOption() {
        return allowAddOption != null ? allowAddOption : false;
    }
}
