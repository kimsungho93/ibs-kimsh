package com.example.demo.suggestion.application.dto;

import com.example.demo.suggestion.domain.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 건의사항 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSuggestionRequest {

    @NotBlank(message = "제목을 입력해주세요")
    @Size(min = 5, max = 100, message = "제목은 5~100자 이내로 입력해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    @Size(min = 10, message = "내용은 최소 10자 이상 입력해주세요")
    private String content;

    @NotNull(message = "카테고리를 선택해주세요")
    private CategoryType category;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    private List<MultipartFile> files = new ArrayList<>();
}
