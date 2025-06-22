package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserStorage userStorage;


    public List<UserDto> findAll() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());

    }


    public UserDto getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId, log));
    }

    public UserDto createUser(NewUserRequest request) {

        // проверяем выполнение необходимых условий
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан", log);
        }
        if (!request.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @", log);
        }
        /*for (UserDto item : findAll()) {
            if (item.getEmail().equals(request.getEmail()))
                throw new DuplicatedDataException("Этот имейл уже используется", log);

        }*/
        if (request.getLogin() == null || request.getLogin().isBlank())
            throw new ValidationException("Логин не может быть пустым", log);
        if (request.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы!", log);
        }
        if (request.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем!", log);
        }

        if (request.getName() == null || request.getName().isBlank())
            request.setName(request.getLogin());

        User user = UserMapper.mapToUser(request);

        Long userId = userStorage.createUser(user);

        return UserMapper.mapToUserDto(userStorage.getUserById(userId).get());

    }

    public UserDto updateUser(UpdateUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан", log);
        }
        if (!request.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @", log);
        }
        if (request.getLogin() == null || request.getLogin().isBlank())
            throw new ValidationException("Логин не может быть пустым", log);
        if (request.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы!", log);
        }
        if (request.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем!", log);
        }
        /*for (User item : userStorage.findAll()) {
            if (item.getEmail().equals(request.getEmail()))
                throw new DuplicatedDataException("Этот имейл уже используется", log);

        }*/
        User updatedUser = userStorage.getUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", log));
        if (updatedUser != null) {
            long updatedUserId = userStorage.updateUser(updatedUser);
            return UserMapper.mapToUserDto(userStorage.getUserById(updatedUserId).get());
        } else
            return null;
    }

    public List<UserDto> getFriendsList(Long userId) {
        if (userStorage.getUserById(userId).isEmpty())
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        return userStorage.getFriendsList(userId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }


    public UserDto addToFriends(Long userId, Long friendId) {
        if (userStorage.getUserById(userId).isEmpty())
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (userStorage.getUserById(friendId).isEmpty())
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден", log);
        boolean isFriend = false;
        for (User friend : userStorage.getFriendsList(userId)) {
            if (friend.getId().equals(friendId)) {
                isFriend = true;
                break;
            }
        }
        if (!isFriend) {
            List<User> friendsList = (List<User>) userStorage.getFriendsList(userId);
            friendsList.add(userStorage.getUserById(friendId).get());
            userStorage.setFriendsList(userId, friendsList);

            return UserMapper.mapToUserDto(userStorage.getUserById(friendId).get());
        } else
            return null;
    }

    public void deleteFromFriends(Long userId, Long friendId) {
        if (userStorage.getUserById(userId).isEmpty())
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (userStorage.getUserById(friendId).isEmpty())
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден", log);

        List<User> friendsList = (List<User>) userStorage.getFriendsList(userId);
        friendsList.remove(userStorage.getUserById(friendId).get());
        userStorage.setFriendsList(userId, friendsList);

    }

    public List<UserDto> getCommonFriendsList(Long userId, Long friendId) {
        if (userStorage.getUserById(userId).isEmpty())
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);
        if (userStorage.getUserById(friendId).isEmpty())
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден", log);

        List<User> friendsList1 = (List<User>) userStorage.getFriendsList(userId);
        List<User> friendsList2 = (List<User>) userStorage.getFriendsList(friendId);

        friendsList1.retainAll(friendsList2);

        return friendsList1.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

}
