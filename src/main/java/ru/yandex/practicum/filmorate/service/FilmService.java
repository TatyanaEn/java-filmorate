package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {

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

    public Film updateFilm(Film film) {
        // проверяем необходимые условия
        if (film.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан", log);
        }
        if (filmStorage.containsId(film.getId())) {
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

            Long filmId = filmStorage.updateFilm(film);
            Film newFilm = filmStorage.getFilmById(filmId);
            log.info("Обновлен фильм {}", film);
            return newFilm;
        }
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден", log);
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }


    public void addLike(Long filmId, Long userId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (!filmStorage.containsId(filmId))
            throw new NotFoundException("Фильм с id = " + filmId + " не найден", log);
        Set<Long> likesList = filmStorage.getLikesList(filmId);
        likesList.add(userId);
        filmStorage.setLikesList(filmId, likesList);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (!filmStorage.containsId(filmId))
            throw new NotFoundException("Фильм с id = " + filmId + " не найден", log);

        Set<Long> likesList = filmStorage.getLikesList(filmId);
        likesList.remove(userId);
        filmStorage.setLikesList(filmId, likesList);
    }

    public Collection<Film> getTopFilms(Integer count) {
        if (count <= 0) {
            throw new ParameterNotValidException("size", "Некорректный размер выборки. Размер должен быть больше нуля");
        }
        return filmStorage.findAll().stream().sorted(new FilmComparatorByLikes()).limit(count).toList();
    }
}