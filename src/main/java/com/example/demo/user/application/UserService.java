package com.example.demo.user.application;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.user.domain.User;
import com.example.demo.user.infra.UserRepository;
import com.example.demo.user.presentation.dto.ActiveUserCountResponse;
import com.example.demo.user.presentation.dto.ActiveUserNamesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }

    public ActiveUserCountResponse getActiveUserCount() {
        long count = userRepository.countByActiveTrue();
        return ActiveUserCountResponse.of(count);
    }

    public ActiveUserNamesResponse getActiveUserNames() {
        List<String> names = userRepository.findByActiveTrue()
                .stream()
                .map(User::getName)
                .toList();

        return ActiveUserNamesResponse.of(names);
    }
}
