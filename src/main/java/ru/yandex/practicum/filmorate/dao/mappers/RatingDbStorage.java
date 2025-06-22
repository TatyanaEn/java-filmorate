package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dao.BaseDbStorage;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

public class RatingDbStorage extends BaseDbStorage<MPA> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM ratings";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM ratings WHERE id = ?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO ratings(name, description)" +
            "VALUES(?, ?)";
    private static final String DELETE_FROM_FILM_GENRE_BY_FILM_ID = "DELETE FROM ratings WHERE id = ?";

    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<MPA> mapper) {
        super(jdbc, mapper);
    }

    public List<MPA> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<MPA> getMPAById(Long mpaId) {
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }


}