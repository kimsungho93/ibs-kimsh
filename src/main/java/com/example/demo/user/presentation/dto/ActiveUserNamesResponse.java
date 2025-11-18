package com.example.demo.user.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 활성 회원 이름 목록 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveUserNamesResponse {

    private List<String> names;

    public static ActiveUserNamesResponse of(List<String> names) {
        return new ActiveUserNamesResponse(names);
    }
}
