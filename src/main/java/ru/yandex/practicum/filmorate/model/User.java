package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"email"})
@Builder
public class User {
    Long id;
    @NotBlank(message = "Логин пользователя не может быть пустым")
    String login;
    @NotBlank(message = "Адрес электронной почты не может быть пустой.")
    @Email(message = "Адрес электронной почты в неверном формате")
    String email;
    String name;
    LocalDate birthday;
}

