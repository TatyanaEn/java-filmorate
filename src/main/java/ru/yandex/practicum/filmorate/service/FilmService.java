package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public List<FilmDto> findAll() {

        return filmStorage.findAll()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto createFilm(NewFilmRequest request) {

        // проверяем выполнение необходимых условий
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым", log);
        }
        if (request.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов", log);
        }
        TemporalAccessor date = DateTimeFormatter.ofPattern("dd.MM.yyyy").parse("28.12.1895");
        if (request.getReleaseDate().isBefore(LocalDate.from(date))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года", log);
        }
        if (request.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом", log);
        }
        if (filmStorage.getMpaById(request.getMpa().getId()).isEmpty()) {
            throw new NotFoundException("Рэйтинг с id = " + request.getMpa().getId() + " не найден", log);
        }

        // формируем дополнительные данные
        Film film = FilmMapper.mapToFilm(request);

        if (!film.getGenres().isEmpty())
            for (Genre genre : film.getGenres()) {
                if (filmStorage.getGenreById(genre.getId()).isEmpty())
                    throw new NotFoundException("Жанр с id = " + genre.getId() + " не найден", log);
            }
        Long filmId = filmStorage.createFilm(film);
        log.info("Добавлен фильм {}", film);
        return FilmMapper.mapToFilmDto(filmStorage.getFilmById(filmId).get());

    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        // проверяем необходимые условия
        if (request.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан", log);
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым", log);
        }
        if (request.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов", log);
        }
        TemporalAccessor date = DateTimeFormatter.ofPattern("dd.MM.yyyy").parse("28.12.1895");
        if (request.getReleaseDate().isBefore(LocalDate.from(date))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года", log);
        }
        if (request.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом", log);
        }
        if (filmStorage.getMpaById(request.getMpa().getId()).isEmpty()) {
            throw new NotFoundException("Рэйтинг с id = " + request.getMpa().getId() + " не найден", log);
        }
        if (!request.getGenres().isEmpty())
            for (Genre genre : request.getGenres()) {
                if (filmStorage.getGenreById(genre.getId()).isEmpty())
                    throw new NotFoundException("Жанр с id = " + genre.getId() + " не найден", log);
            }

        Film updatedFilm = filmStorage.getFilmById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + request.getId() + " не найден", log));


        if (updatedFilm != null) {
            long updatedFilmId = filmStorage.updateFilm(updatedFilm);
            log.info("Обновлен фильм {}", updatedFilm);
            return FilmMapper.mapToFilmDto(filmStorage.getFilmById(updatedFilmId).get());
        } else
            return null;
    }

    public FilmDto getFilmById(Long filmId) {

        return filmStorage.getFilmById(filmId)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден", log));

    }


    public void addLike(Long filmId, Long userId) {
        if (userStorage.getUserById(userId).isEmpty())
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (filmStorage.getFilmById(filmId).isEmpty())
            throw new NotFoundException("Фильм с id = " + filmId + " не найден", log);
        Set<Long> likesList = filmStorage.getLikesList(filmId);
        likesList.add(userId);
        filmStorage.setLikesList(filmId, likesList);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (userStorage.getUserById(userId).isEmpty())
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (filmStorage.getFilmById(filmId).isEmpty())
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

    public GenreDto getGenreById(Long genreId) {
        return filmStorage.getGenreById(genreId)
                .map(GenreMapper::mapToGenreDto)
                .orElseThrow(() -> new NotFoundException("Жанр с id = " + genreId + " не найден", log));
    }

    public Collection<GenreDto> findAllGenre() {
        return filmStorage.findAllGenre().stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public MpaDto getMpaById(Long mpaId) {
        return filmStorage.getMpaById(mpaId)
                .map(MpaMapper::mapToMpaDto)
                .orElseThrow(() -> new NotFoundException("Рэйтинг с id = " + mpaId + " не найден", log));
    }

    public Collection<MpaDto> findAllMpa() {
        return filmStorage.findAllMpa().stream()
                .map(MpaMapper::mapToMpaDto)
                .collect(Collectors.toList());
    }


}