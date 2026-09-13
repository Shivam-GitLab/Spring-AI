package com.spring.ai.api.dtos.records;

import java.util.List;

public record User(int userId, String name, double age, String birthdate, List<String> Likes) {
}
