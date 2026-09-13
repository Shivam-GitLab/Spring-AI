package com.spring.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.ai.api.dtos.records.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final List<User> users;

    public UserService() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        InputStream inputStream =
                getClass()
                        .getClassLoader()
                        .getResourceAsStream("data/users.json");

        if (inputStream == null) {
            throw new IllegalStateException(
                    "users.json not found in src/main/resources/data/"
            );
        }

        users = objectMapper.readValue(
                inputStream,
                new TypeReference<List<User>>() {}
        );

        log.info(
                "Loaded {} users from users.json",
                users.size()
        );
    }

    @Tool(description = "Get all users")
    public List<User> getAll() {

        log.info("Tool called for get all users...");

        return users;
    }

    @Tool(description = "Get all users by given age")
    public List<User> getUsersByAge(double age) {

        log.info(
                "Tool called for users by age: {}",
                age
        );

        return users.stream()
                .filter(user -> user.age() == age)
                .toList();
    }

    @Tool(description = "Get a user by id")
    public User getUserById(int userId) {

        log.info(
                "Tool called for user id: {}",
                userId
        );

        return users.stream()
                .filter(user -> user.userId() == userId)
                .findFirst()
                .orElse(null);
    }
}