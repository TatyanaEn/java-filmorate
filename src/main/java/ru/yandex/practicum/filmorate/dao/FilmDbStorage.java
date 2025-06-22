package ru.yandex.practicum.filmorate.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.RatingDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Repository
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, duration, releasedate, rating_id)     " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, duration = ?, releasedate = ?, rating_id = ? WHERE id = ?";
    private static final String INSERT_FILM_LIKE = "INSERT INTO likes(film_id, user_id)" +
            "VALUES(?, ?)";
    private static final String FIND_FILM_LIKE = "SELECT user_id FROM likes WHERE FILM_ID = ?";
    private final GenreDbStorage genreDbStorage;
    private final RatingDbStorage ratingDbStorage;


    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
        this.genreDbStorage = new GenreDbStorage(jdbc, new GenreRowMapper());
        this.ratingDbStorage = new RatingDbStorage(jdbc, new MpaRowMapper());
    }

    @Override
    public List<Film> findAll() {
        List<Film> filmList = findMany(FIND_ALL_QUERY);
        for (Film film : filmList) {
            film.setGenres(genreDbStorage.getFilmGenre(film.getId()));
            film.setMpa(getMpaById(film.getMpa().getId()).isEmpty() ? null : getMpaById(film.getMpa().getId()).get());
            film.setLikes(getLikesList(film.getId()));
        }

        return filmList;
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        Film film = findOne(FIND_BY_ID_QUERY, filmId).get();
        film.setGenres(genreDbStorage.getFilmGenre(filmId));
        film.setMpa(getMpaById(film.getMpa().getId()).isEmpty() ? null : getMpaById(film.getMpa().getId()).get());
        film.setLikes(getLikesList(film.getId()));
        return Optional.of(film);
    }

    @Override
    public Long createFilm(Film film) {

        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa() == null ? null : film.getMpa().getId()
        );
        film.setId(id);
        if (film.getGenres() != null) {
            TreeSet<Long> setGenreId = new TreeSet<>();
            for (Genre genre : film.getGenres()) {
                setGenreId.add(genre.getId());
            }
            for (Long genreId : setGenreId) {
                genreDbStorage.addFilmGenre(film.getId(), genreId);
            }
        }
        setLikesList(film.getId(), film.getLikes());
        return film.getId();
    }

    @Override
    public Long updateFilm(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa() == null ? null : film.getMpa().getId(),
                film.getId()
        );
        genreDbStorage.deleteFromFilmGenreByFilmId(film.getId());
        if (film.getGenres() != null) {
            TreeSet<Long> setGenreId = new TreeSet<>();
            for (Genre genre : film.getGenres()) {
                setGenreId.add(genre.getId());
            }
            for (Long genreId : setGenreId) {
                genreDbStorage.addFilmGenre(film.getId(), genreId);
            }
        }
        setLikesList(film.getId(), film.getLikes());
        return film.getId();
    }

    @Override
    public Set<Long> getLikesList(Long filmId) {
        List<Long> likes = jdbc.queryForList(FIND_FILM_LIKE, Long.class, filmId);
        return new HashSet<>(likes);
    }

    private void clearLikes(Long filmId) {
        String sqlQuery = "DELETE FROM likes WHERE FILM_ID = ?";
        jdbc.update(sqlQuery, filmId);
    }

    @Override
    public void setLikesList(Long filmId, Set<Long> likesList) {
        clearLikes(filmId);
        if (likesList != null)
            for (Long like : likesList) {
                insert(INSERT_FILM_LIKE, filmId, like);
            }

    }

    @Override
    public List<Genre> findAllGenre() {
        return genreDbStorage.findAll();
    }

    @Override
    public Optional<Genre> getGenreById(Long genreId) {
        return genreDbStorage.getGenreById(genreId);
    }

    @Override
    public List<MPA> findAllMpa() {
        return ratingDbStorage.findAll();
    }

    @Override
    public Optional<MPA> getMpaById(Long mpaId) {
        return ratingDbStorage.getMPAById(mpaId);
    }


}
