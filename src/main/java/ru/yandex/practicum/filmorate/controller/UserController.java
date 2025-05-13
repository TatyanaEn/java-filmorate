package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан", log);
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @", log);
        }
        for (User item : users.values()) {
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

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан", log);
        }
        if (users.containsKey(newUser.getId())) {
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                throw new ConditionsNotMetException("Имейл должен быть указан", log);
            }
            if (!newUser.getEmail().contains("@")) {
                throw new ValidationException("Электронная почта должна содержать символ @", log);
            }
            for (User item : users.values()) {
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

            User oldUser = users.get(newUser.getId());
            if (!(newUser.getEmail() == null || newUser.getEmail().isBlank()))
                oldUser.setEmail(newUser.getEmail());
            if (!(newUser.getName() == null || newUser.getName().isBlank()))
                oldUser.setName(newUser.getName());
            else
                oldUser.setName(newUser.getLogin());
            if (!(newUser.getLogin() == null || newUser.getLogin().isBlank()))
                oldUser.setLogin(newUser.getLogin());
            if (newUser.getBirthday() != null)
                oldUser.setBirthday(newUser.getBirthday());
            log.info("Обновлен пользователь {}", oldUser);

            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден", log);
    }

}
