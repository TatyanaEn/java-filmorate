package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FilmDbStorage.class, FilmRowMapper.class})
public class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testCreateUser() {

        User user = User.builder()
                .name("test1")
                .login("testLogin")
                .email("test@test.ru")
                .birthday(LocalDate.of(1980, 10, 25))
                .build();
        Long id = userStorage.createUser(user);
        user.setId(id);
        Optional<User> savedUser = userStorage.getUserById(id);

        assertNotNull(savedUser.get(), "Пользователь не найден.");

        assertEquals(user, savedUser.get(), "Пользователи не совпадают.");

    }

    @Test
    public void testUpdateUser() {

        User user = User.builder()
                .name("test1")
                .login("testLogin")
                .email("test@test.ru")
                .birthday(LocalDate.of(1980, 10, 25))
                .build();
        Long id = userStorage.createUser(user);
        user.setId(id);

        user.setName("Updated_name");
        userStorage.updateUser(user);
        Optional<User> savedUser = userStorage.getUserById(id);

        assertNotNull(savedUser.get(), "Пользователь не найден.");

        assertEquals(user, savedUser.get(), "Пользователи не совпадают.");

    }

    @Test
    public void testFindUserById() {
        User user = User.builder()
                .name("test1")
                .login("testLogin")
                .email("test@test.ru")
                .birthday(LocalDate.of(1980, 10, 25))
                .build();
        Long id = userStorage.createUser(user);
        user.setId(id);
        Optional<User> savedUser = userStorage.getUserById(id);
        assertEquals(user, savedUser.get(), "Пользователи не совпадают.");

    }

    @Test
    public void testFindAllUser() {
        User user1 = User.builder()
                .name("test1")
                .login("test1Login")
                .email("test1@test.ru")
                .birthday(LocalDate.of(1980, 10, 25))
                .build();
        Long id = userStorage.createUser(user1);

        User user2 = User.builder()
                .name("test2")
                .login("test2Login")
                .email("test2@test.ru")
                .birthday(LocalDate.of(1980, 10, 25))
                .build();
        id = userStorage.createUser(user2);

        List<User> userList = userStorage.findAll();

        assertEquals(2, userList.size(), "Неверное количество пользователей.");
    }

    @Test
    public void testAddFriend() {

        User user1 = User.builder()
                .name("test1")
                .login("test1Login")
                .email("test1@test.ru")
                .birthday(LocalDate.of(1980, 10, 25))
                .build();
        Long id1 = userStorage.createUser(user1);

        User user2 = User.builder()
                .name("test2")
                .login("test2Login")
                .email("test2@test.ru")
                .birthday(LocalDate.of(1980, 10, 25))
                .build();
        Long id2 = userStorage.createUser(user2);

        List<User> friendsList = new ArrayList<>();
        friendsList.add(user2);
        userStorage.setFriendsList(id1, friendsList);

        List<User> savedFriendsList = userStorage.getFriendsList(id1);

        assertEquals(friendsList, savedFriendsList, "список Друзей не совпадает.");

    }

    @Test
    public void testDeleteFriend() {

        User user1 = User.builder()
                .name("test1")
                .login("test1Login")
                .email("test1@test.ru")
                .birthday(LocalDate.of(1980, 10, 25))
                .build();
        Long id1 = userStorage.createUser(user1);

        User user2 = User.builder()
                .name("test2")
                .login("test2Login")
                .email("test2@test.ru")
                .birthday(LocalDate.of(1980, 10, 25))
                .build();
        Long id2 = userStorage.createUser(user2);

        List<User> friendsList = new ArrayList<>();
        friendsList.add(user2);
        userStorage.setFriendsList(id1, friendsList);
        friendsList.remove(user2);
        userStorage.setFriendsList(id1, friendsList);

        List<User> savedFriendsList = userStorage.getFriendsList(id1);

        assertEquals(savedFriendsList.size(), 0, "Список друзей не пуст");

        assertEquals(friendsList, savedFriendsList, "список Друзей не совпадает.");

    }

    @Test
    public void testCreateFilm() {

        Film film = Film.builder()
                .name("test1")
                .description("test1")
                .duration(100)
                .releaseDate(LocalDate.of(1980, 10, 25))
                .genres(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
        Long id = filmStorage.createFilm(film);
        film.setId(id);
        Optional<Film> sevedFilm = filmStorage.getFilmById(id);

        assertNotNull(sevedFilm.get(), "Фильм не найден.");

        assertEquals(film, sevedFilm.get(), "Фильмы не совпадают.");

    }

    @Test
    public void testUpdateFilm() {

        Film film = Film.builder()
                .name("test1")
                .description("test1")
                .duration(100)
                .releaseDate(LocalDate.of(1980, 10, 25))
                .genres(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
        Long id = filmStorage.createFilm(film);
        film.setId(id);
        Optional<Film> sevedFilm = filmStorage.getFilmById(id);

        film.setName("Updated_name");
        filmStorage.updateFilm(film);
        Optional<Film> savedFilm = filmStorage.getFilmById(id);

        assertNotNull(savedFilm.get(), "Фильм не найден.");

        assertEquals(film, savedFilm.get(), "Фильмы не совпадают.");


    }

    @Test
    public void testFindFilmById() {
        Film film = Film.builder()
                .name("test1")
                .description("test1")
                .duration(100)
                .releaseDate(LocalDate.of(1980, 10, 25))
                .genres(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
        Long id = filmStorage.createFilm(film);
        film.setId(id);
        Optional<Film> savedFilm = filmStorage.getFilmById(id);
        assertEquals(film, savedFilm.get(), "Фильмы не совпадают.");

    }

    @Test
    public void testFindAllFilm() {
        Film film1 = Film.builder()
                .name("test1")
                .description("test1")
                .duration(100)
                .releaseDate(LocalDate.of(1980, 10, 25))
                .genres(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
        filmStorage.createFilm(film1);

        Film film2 = Film.builder()
                .name("test2")
                .description("test2")
                .duration(100)
                .releaseDate(LocalDate.of(1980, 10, 25))
                .genres(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
        filmStorage.createFilm(film2);

        List<Film> filmList = filmStorage.findAll();

        assertEquals(2, filmList.size(), "Неверное количество фильмов.");
    }
}