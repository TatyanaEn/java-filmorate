package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
        this.userService = new UserService(userStorage);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан", log);
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @", log);
        }
        for (User item : userStorage.findAll()) {
            if (item.getEmail().equals(user.getEmail()))
                throw new DuplicatedDataException("Этот имейл уже используется", log);

        }
        if (user.getLogin() == null || user.getLogin().isBlank())
            throw new ValidationException("Логин не может быть пустым", log);
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы!", log);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем!", log);
        }

        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());

        Long newId = userStorage.createUser(user);
        user.setId(newId);
        log.info("Добавлен пользователь {}", user);
        return user;
    }


    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан", log);
        }
        if (userStorage.containsId(newUser.getId())) {
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                throw new ConditionsNotMetException("Имейл должен быть указан", log);
            }
            if (!newUser.getEmail().contains("@")) {
                throw new ValidationException("Электронная почта должна содержать символ @", log);
            }
            for (User item : userStorage.findAll()) {
                if (item.getEmail().equals(newUser.getEmail()))
                    throw new DuplicatedDataException("Этот имейл уже используется", log);

            }
            if (newUser.getLogin() == null || newUser.getLogin().isBlank())
                throw new ValidationException("Логин не может быть пустым", log);
            if (newUser.getLogin().contains(" ")) {
                throw new ValidationException("Логин не может содержать пробелы!", log);
            }
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем!", log);
            }

            Long userId = userStorage.updateUser(newUser);
            User user = userStorage.getUserById(userId);

            log.info("Обновлен пользователь {}", user);

            return user;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден", log);
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable("userId") Long userId) {
        return userStorage.getUserById(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addToFriends(@PathVariable("userId") Long userId,
                             @PathVariable("friendId") Long friendId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (!userStorage.containsId(friendId))
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден", log);
        userService.addToFriends(userId, friendId);
        return userStorage.getUserById(friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable("userId") Long userId,
                                  @PathVariable("friendId") Long friendId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (!userStorage.containsId(friendId))
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден", log);
        userService.deleteFromFriends(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public ArrayList<User> getFriends(@PathVariable("userId") Long userId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        return userStorage.getFriendsList(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public ArrayList<User> getCommonFriends(@PathVariable("userId") Long userId,
                                            @PathVariable("otherId") Long otherId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (!userStorage.containsId(otherId))
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден", log);
        return userService.getCommonFriendsList(userId, otherId);
    }


}
