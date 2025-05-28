package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final FilmService filmService;

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public FilmController(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmService = new FilmService(filmStorage);
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody Film film) {
        // проверяем выполнение необходимых условий
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым", log);
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов", log);
        }
        TemporalAccessor date = DateTimeFormatter.ofPattern("dd.MM.yyyy").parse("28.12.1895");
        if (film.getReleaseDate().isBefore(LocalDate.from(date))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года", log);
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом", log);
        }
        // формируем дополнительные данные

        Long newId = filmStorage.createFilm(film);
        film.setId(newId);

        log.info("Добавлен фильм {}", film);
        return film;
    }


    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        // проверяем необходимые условия
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан", log);
        }
        if (filmStorage.containsId(newFilm.getId())) {
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                throw new ValidationException("Название не может быть пустым", log);
            }
            if (newFilm.getDescription().length() > 200) {
                throw new ValidationException("Максимальная длина описания — 200 символов", log);
            }
            TemporalAccessor date = DateTimeFormatter.ofPattern("dd.MM.yyyy").parse("28.12.1895");
            if (newFilm.getReleaseDate().isBefore(LocalDate.from(date))) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года", log);
            }
            if (newFilm.getDuration() <= 0) {
                throw new ValidationException("Продолжительность фильма должна быть положительным числом", log);
            }

            Long filmId = filmStorage.updateFilm(newFilm);
            Film film = filmStorage.getFilmById(filmId);
            log.info("Обновлен фильм {}", film);
            return film;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден", log);
    }

    @GetMapping("/{filmId}")
    public Film findById(@PathVariable("filmId") Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable("filmId") Long filmId,
                        @PathVariable("userId") Long userId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (!filmStorage.containsId(filmId))
            throw new NotFoundException("Фильм с id = " + filmId + " не найден", log);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable("filmId") Long filmId,
                           @PathVariable("userId") Long userId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (!filmStorage.containsId(filmId))
            throw new NotFoundException("Фильм с id = " + filmId + " не найден", log);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopList(@RequestParam(defaultValue = "10") Integer count) {
        if (count <= 0) {
            throw new ParameterNotValidException("size", "Некорректный размер выборки. Размер должен быть больше нуля");
        }
        return filmService.getTopFilms(count);
    }


}
