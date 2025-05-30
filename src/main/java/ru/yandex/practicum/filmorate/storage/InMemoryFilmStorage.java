package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
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
    public Boolean containsId(Long filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public Film getFilmById(Long filmId) {
        Film filmFS = films.get(filmId);
        return Film.builder()
                .id(filmFS.getId())
                .name(filmFS.getName())
                .releaseDate(filmFS.getReleaseDate())
                .description(filmFS.getDescription())
                .duration(filmFS.getDuration())
                .likes(filmFS.getLikes())
                .build();
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


    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
