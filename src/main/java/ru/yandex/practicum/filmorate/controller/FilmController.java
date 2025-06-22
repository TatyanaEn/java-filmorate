package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/films")
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @PostMapping(value = "/films")
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@RequestBody NewFilmRequest request) {
        return filmService.createFilm(request);
    }


    @PutMapping(value = "/films")
    public FilmDto update(@RequestBody UpdateFilmRequest request) {
        return filmService.updateFilm(request);
    }

    @GetMapping("/films/{filmId}")
    public FilmDto findById(@PathVariable("filmId") Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public void addLike(@PathVariable("filmId") Long filmId,
                        @PathVariable("userId") Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable("filmId") Long filmId,
                           @PathVariable("userId") Long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getTopList(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getTopFilms(count);
    }

    @GetMapping("/genres")
    public Collection<GenreDto> findAllGenre() {
        return filmService.findAllGenre();
    }

    @GetMapping("/genres/{id}")
    public GenreDto findGenreById(@PathVariable("id") Long genreId) {
        return filmService.getGenreById(genreId);
    }

    @GetMapping("/mpa")
    public Collection<MpaDto> findAllMpa() {
        return filmService.findAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public MpaDto findMpaById(@PathVariable("id") Long mpaId) {
        return filmService.getMpaById(mpaId);
    }

}
