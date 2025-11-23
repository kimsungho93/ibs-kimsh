package com.example.demo.chat.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateRequest {

    @NotBlank(message = "채팅방 이름은 필수입니다.")
    @Size(max = 30, message = "채팅방 이름은 최대 30자까지 가능합니다.")
    private String name;

    @Size(max = 20, message = "비밀번호는 최대 20자까지 가능합니다.")
    private String password;

    @Min(value = 2, message = "최소 인원은 2명입니다.")
    @Max(value = 15, message = "최대 인원은 15명입니다.")
    private Integer maxParticipants;
}
