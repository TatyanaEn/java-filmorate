package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"email"})
@Builder
public class User {
    Long id;
    String login;
    String email;
    String name;
    LocalDate birthday;
    private Set<Long> friends;

}

