package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;

    private final Map<Long, Genre> genres;

    private final Map<Long, MPA> mpa;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
        this.genres = new HashMap<>();
        this.mpa = new HashMap<>();
    }

    @Override
    public Collection<Film> findAll() {

        return films.values().stream()
                .map(film -> Film.builder()
                        .id(film.getId())
                        .name(film.getName())
                        .releaseDate(film.getReleaseDate())
                        .description(film.getDescription())
                        .duration(film.getDuration())
                        .likes(film.getLikes())
                        .build())
                .toList();
    }

    @Override
    public Long createFilm(Film film) {
        Film newFilm = Film.builder()
                .id(getNextId())
                .name(film.getName())
                .releaseDate(film.getReleaseDate())
                .description(film.getDescription())
                .duration(film.getDuration())
                .likes(film.getLikes())
                .build();
        films.put(newFilm.getId(), newFilm);

        return newFilm.getId();
    }

    @Override
    public Long updateFilm(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDuration(newFilm.getDuration());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        films.put(oldFilm.getId(), oldFilm);
        return oldFilm.getId();
    }


    @Override
    public Optional<Film> getFilmById(Long filmId) {
        Film filmFS = films.get(filmId);
        if (filmFS == null)
            return Optional.empty();
        return Optional.ofNullable(Film.builder()
                .id(filmFS.getId())
                .name(filmFS.getName())
                .releaseDate(filmFS.getReleaseDate())
                .description(filmFS.getDescription())
                .duration(filmFS.getDuration())
                .likes(filmFS.getLikes())
                .build());
    }

    @Override
    public Set<Long> getLikesList(Long filmId) {
        if (films.get(filmId).getLikes() == null)
            return new HashSet<>();
        else
            return films.get(filmId).getLikes();
    }

    @Override
    public void setLikesList(Long filmId, Set<Long> likesList) {
        if (likesList != null)
            films.get(filmId).setLikes(likesList);
    }

    @Override
    public List<Genre> findAllGenre() {
        return genres.values().stream()
                .map(genre -> Genre.builder()
                        .id(genre.getId())
                        .name(genre.getName())
                        .build())
                .toList();
    }


    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Optional<Genre> getGenreById(Long genreId) {
        Genre genreFS = genres.get(genreId);
        if (genreFS == null)
            return Optional.empty();
        return Optional.ofNullable(Genre.builder()
                .id(genreFS.getId())
                .name(genreFS.getName())
                .build());
    }

    @Override
    public List<MPA> findAllMpa() {
        return mpa.values().stream()
                .map(mpa -> MPA.builder()
                        .id(mpa.getId())
                        .name(mpa.getName())
                        .build())
                .toList();
    }

    @Override
    public Optional<MPA> getMpaById(Long mpaId) {
        MPA mpaFS = mpa.get(mpaId);
        if (mpaFS == null)
            return Optional.empty();
        return Optional.ofNullable(MPA.builder()
                .id(mpaFS.getId())
                .name(mpaFS.getName())
                .name(mpaFS.getName())
                .build());
    }

}
