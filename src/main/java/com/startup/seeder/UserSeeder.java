package com.startup.seeder;

import com.model.User;
import com.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("dev")
@Order(0)
@Component
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    @Override
    public void run(String... args) throws Exception {
        User user1 = User.builder()
                .admin(true)
                .email("ivan123@gmail.com")
                .passwordHash("123")
                .phoneNumber("08789564")
                .name("Ivan")
                .username("ivan123")
                .build();

        userRepository.save(user1);
    }


}
