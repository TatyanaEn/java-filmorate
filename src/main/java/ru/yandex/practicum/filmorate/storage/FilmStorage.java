package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> findAll();

    Long createFilm(Film film);

    Long updateFilm(Film film);

    Boolean containsId(Long filmId);

    Film getFilmById(Long filmId);

    Set<Long> getLikesList(Long filmId);

    void setLikesList(Long filmId, Set<Long> likesList);

}
