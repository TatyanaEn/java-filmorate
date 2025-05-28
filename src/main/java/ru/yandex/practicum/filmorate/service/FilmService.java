package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

@Service
public class FilmService {

    private final FilmStorage filmStorage;


    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Long filmId, Long userId) {
        Set<Long> likesList = filmStorage.getLikesList(filmId);
        likesList.add(userId);
        filmStorage.setLikesList(filmId, likesList);
    }

    public void deleteLike(Long filmId, Long userId) {
        Set<Long> likesList = filmStorage.getLikesList(filmId);
        likesList.remove(userId);
        filmStorage.setLikesList(filmId, likesList);
    }

    public Collection<Film> getTopFilms(Integer count) {
        return filmStorage.findAll().stream().sorted(new FilmComparatorByLikes()).limit(count).toList();
    }

}

class FilmComparatorByLikes implements Comparator<Film> {

    public int compare(Film a, Film b) {

        return ((b.getLikes() == null ? 0 : b.getLikes().size()) - (a.getLikes() == null ? 0 : a.getLikes().size()));
    }
}
