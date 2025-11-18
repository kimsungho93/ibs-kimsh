package com.example.demo.user.presentation;

import com.example.demo.user.application.UserService;
import com.example.demo.user.presentation.dto.ActiveUserNamesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 활성 회원 이름 목록 조회
     * @return 활성 회원 이름 목록
     */
    @GetMapping("/active/names")
    public ResponseEntity<ActiveUserNamesResponse> getActiveUserNames() {
        return ResponseEntity.ok(userService.getActiveUserNames());
    }
}
