package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String GET_GENRE_FILM = "SELECT g.GENRE_ID , g.name FROM FILM_GENRE fg , GENRE g \n" +
            "WHERE fg.GENRE_ID   = g.GENRE_ID AND fg.FILM_ID=?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID)" +
            "VALUES(?, ?)";
    private static final String DELETE_FROM_FILM_GENRE_BY_FILM_ID = "DELETE FROM FILM_GENRE WHERE film_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> getGenreById(Long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public List<Genre> getFilmGenre(Long filmId) {
        return findMany(GET_GENRE_FILM, filmId);
    }

    public void addFilmGenre(Long filmId, Long genreId) {
        insert(
                INSERT_FILM_GENRE,
                filmId,
                genreId
        );
    }

    public void deleteFromFilmGenreByFilmId(Long filmId) {
        delete(DELETE_FROM_FILM_GENRE_BY_FILM_ID, filmId);
    }

}
