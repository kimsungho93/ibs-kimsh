package com.example.demo.user.application;

import com.example.demo.user.infra.UserRepository;
import com.example.demo.user.presentation.dto.ActiveUserNamesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사용자 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 활성 회원 이름 목록 조회
     * - active = true인 회원들의 이름만 반환
     */
    public ActiveUserNamesResponse getActiveUserNames() {
        List<String> names = userRepository.findByActiveTrue()
                .stream()
                .map(user -> user.getName())
                .toList();

        return ActiveUserNamesResponse.of(names);
    }
}
