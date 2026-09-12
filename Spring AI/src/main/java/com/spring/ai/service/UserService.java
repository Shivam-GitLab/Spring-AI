package com.spring.ai.service;

import com.spring.ai.api.dtos.records.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final List<User> users = List.of(
            new User(1, "Alice", 28),
            new User(2, "Bob", 34),
            new User(3, "Charlie", 28),
            new User(4, "Diana", 41),
            new User(5, "Ethan", 34)
    );

    @Tool(description = "Get all users")
    public List<User> getAll() {
        log.info("Tool called for get all users...");
        log.info("Fetching all users, count={}", users.size());
        return users;
    }

    @Tool(description = "Get all users by given age")
    public List<User> getUsersByAge(int age) {
        log.info("Tool called for get all users by age...");
        log.info("Fetching users with age={}", age);
        return users.stream()
                .filter(user -> user.age() == age)
                .toList();
    }

    @Tool(description = "Get a user by id")
    public User getUserById(int userId) {
        log.info("Tool called for get a user by id...");
        log.info("Fetching user with id={}", userId);
        return users.stream()
                .filter(user -> user.userId() == userId)
                .findFirst()
                .orElse(null);
    }
}
