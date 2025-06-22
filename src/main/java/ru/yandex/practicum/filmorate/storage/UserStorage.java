package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Long createUser(User user);

    Long updateUser(User user);

    Optional<User> getUserById(Long userId);

    Collection<User> getFriendsList(Long userId);

    void setFriendsList(Long userId, List<User> friendsList);


}
