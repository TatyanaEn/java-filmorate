package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateUserRequest {
    private Long id;
    private String login;
    private String email;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends;


    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasBirthday() {
        return !(birthday == null);
    }

    public boolean hasFriends() {
        return !(friends == null || friends.isEmpty());
    }
}