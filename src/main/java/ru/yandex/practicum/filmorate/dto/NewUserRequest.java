package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class NewUserRequest {
    private String login;
    private String email;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends;
}