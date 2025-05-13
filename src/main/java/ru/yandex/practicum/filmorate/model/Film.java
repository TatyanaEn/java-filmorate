package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */

@Data
@Builder
public class Film {
    Long id;
    @NotBlank(message = "Название фильма  не может быть пустым")
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
}
