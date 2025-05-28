package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmComparatorByLikes implements Comparator<Film> {

    public int compare(Film a, Film b) {

        return ((b.getLikes() == null ? 0 : b.getLikes().size()) - (a.getLikes() == null ? 0 : a.getLikes().size()));
    }
}