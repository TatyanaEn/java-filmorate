package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> findAll();

    Long createFilm(Film film);

    Long updateFilm(Film film);

    Optional<Film> getFilmById(Long filmId);

    Set<Long> getLikesList(Long filmId);

    void setLikesList(Long filmId, Set<Long> likesList);

    List<Genre> findAllGenre();

    Optional<Genre> getGenreById(Long genreId);

    List<MPA> findAllMpa();

    Optional<MPA> getMpaById(Long mpaId);

}
