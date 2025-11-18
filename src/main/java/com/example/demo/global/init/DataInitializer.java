package com.example.demo.global.init;

import com.example.demo.user.domain.User;
import com.example.demo.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 초기 데이터 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUsersIfNotExists();
    }

    /**
     * 초기 사용자 생성
     */
    private void createUsersIfNotExists() {
        Object[][] users = {
                {"정태원", "twjung@ibslab.com", "twjung1234", User.Role.USER},
                {"최철종", "cjchoi@ibslab.com", "cjchoi1234", User.Role.USER},
                {"박으뜸", "empark@ibslab.com", "empark1234", User.Role.USER},
                {"선도우", "douseon@ibslab.com", "douseon1234", User.Role.USER},
                {"육이슬", "isyuk@ibslab.com", "isyuk1234", User.Role.USER},
                {"유영진", "yjyou@ibslab.com", "yjyou1234", User.Role.USER},
                {"이정규", "jglee@ibslab.com", "jglee1234", User.Role.USER},
                {"허소영", "syheo@ibslab.com", "syheo1234", User.Role.USER},
                {"김예린", "yrkim@ibslab.com", "yrkim1234", User.Role.USER},
                {"김성호", "kimsh@ibslab.com", "dlekdud3", User.Role.ADMIN},
                {"길기호", "ghgil@ibslab.com", "ghgil1234", User.Role.USER},
                {"이다혜", "leedh@ibslab.com", "leedh1234", User.Role.USER},
                {"김현진", "kimhj@ibslab.com", "kimhj1234", User.Role.USER},
                {"문형석", "hsmun@ibslab.com", "hsmun1234", User.Role.USER},
                {"박찬진", "cjpark@ibslab.com", "cjpark1234", User.Role.USER},
                {"김성호", "ksh@ibslab.com", "ksh1234", User.Role.USER}
        };

        for (Object[] userData : users) {
            String name = (String) userData[0];
            String email = (String) userData[1];
            String password = (String) userData[2];
            User.Role role = (User.Role) userData[3];

            if (!userRepository.existsByEmail(email)) {
                User user = User.builder()
                        .name(name)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .role(role)
                        .active(true)
                        .build();

                userRepository.save(user);
                log.info("사용자 생성 완료: {} ({})", name, email);
            }
        }
    }
}
