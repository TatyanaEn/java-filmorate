package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    private User user;
    private UserService userService;

    private InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    public void beforeEach() {

        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        user = User.builder()
                .name("MyName")
                .login("MaxPower")
                .email("1@ya.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();
    }

    // проверка контроллера при корректных атрибутах пользователя
    @Test
    public void shouldAddUserWhenAllAttributeCorrect() {
        User user1 = userService.createUser(user);
        assertEquals(user, user1, "Переданный и полученный пользователь должны совпадать");
        assertEquals(1, userService.findAll().size(), "В списке должен быть один пользователь");
    }

    // проверка контроллера, когда электронная почта пустая
    @Test
    public void shouldNoAddUserWhenUserEmailIsEmpty() {
        user.setEmail("");
        assertThrows(ConditionsNotMetException.class, () -> userService.createUser(user));
        assertEquals(0, userService.findAll().size(), "Список пользователей должен быть пустым");
    }

    // проверка контроллера, когда электронная почта не содержит символа @
    @Test
    public void shouldNoAddUserWhenUserEmailIsNotContainsCommercialAt() {
        user.setEmail("bademail.ru");
        assertThrows(ValidationException.class, () -> userService.createUser(user));
        assertEquals(0, userService.findAll().size(), "Список пользователей должен быть пустым");
    }

    // проверка контроллера, когда у пользователя пустой логин
    @Test
    public void shouldNoAddUserWhenUserLoginIsEmpty() {
        user.setLogin("");
        assertThrows(ValidationException.class, () -> userService.createUser(user));
        assertEquals(0, userService.findAll().size(), "Список пользователей должен быть пустым");
    }

    // проверка контроллера, когда логин пользователя содержит пробелы
    @Test
    public void shouldNoAddUserWhenUserLoginIsContainsSpaces() {
        user.setLogin("Max Power");
        assertThrows(ValidationException.class, () -> userService.createUser(user));
        assertEquals(0, userService.findAll().size(), "Список пользователей должен быть пустым");
    }

    // проверка контроллера, когда имя пользователя пустое
    @Test
    public void shouldAddUserWhenUserNameIsEmpty() {
        user.setName("");
        User user1 = userService.createUser(user);
        assertTrue(user1.getName().equals(user.getLogin()),
                "Имя и логин пользователя должны совпадать");
        assertEquals(1, userService.findAll().size(), "В списке должен быть один пользователь");
    }

    // проверка контроллера, когда дата рождения пользователя в будущем
    @Test
    public void shouldAddUserWhenUserBirthdayInFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userService.createUser(user));
        assertEquals(0, userService.findAll().size(), "Список пользователей должен быть пустым");
    }


}
