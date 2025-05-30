package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserStorage userStorage;


    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(Long userId) {
        if (userStorage.containsId(userId)) {
            return userStorage.getUserById(userId);
        }
        throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
    }

    public User createUser(User user) {

        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан", log);
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @", log);
        }
        for (User item : findAll()) {
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

        return user;
    }


    public User updateUser(User user) {

        // проверяем необходимые условия
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан", log);
        }
        if (userStorage.containsId(user.getId())) {
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

            Long userId = userStorage.updateUser(user);
            User newUser = userStorage.getUserById(userId);

            log.info("Обновлен пользователь {}", newUser);

            return newUser;
        }
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден", log);
    }

    public ArrayList<User> getFriendsList(Long userId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        return userStorage.getFriendsList(userId);
    }


    public User addToFriends(Long userId, Long friendId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (!userStorage.containsId(friendId))
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден", log);

        ArrayList<User> friendsList = userStorage.getFriendsList(userId);
        friendsList.add(userStorage.getUserById(friendId));
        userStorage.setFriendsList(userId, friendsList);

        friendsList = userStorage.getFriendsList(friendId);
        friendsList.add(userStorage.getUserById(userId));
        userStorage.setFriendsList(friendId, friendsList);
        return userStorage.getUserById(friendId);
    }

    public void deleteFromFriends(Long userId, Long friendId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (!userStorage.containsId(friendId))
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден", log);

        ArrayList<User> friendsList = userStorage.getFriendsList(userId);
        friendsList.remove(userStorage.getUserById(friendId));
        userStorage.setFriendsList(userId, friendsList);

        friendsList = userStorage.getFriendsList(friendId);
        friendsList.remove(userStorage.getUserById(userId));
        userStorage.setFriendsList(friendId, friendsList);
    }

    public ArrayList<User> getCommonFriendsList(Long userId, Long friendId) {
        if (!userStorage.containsId(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (!userStorage.containsId(friendId))
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден", log);

        ArrayList<User> friendsList1 = userStorage.getFriendsList(userId);
        ArrayList<User> friendsList2 = userStorage.getFriendsList(friendId);

        friendsList1.retainAll(friendsList2);
        return friendsList1;
    }

}
