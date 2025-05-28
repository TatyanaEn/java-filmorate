package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    Long createUser(User user);

    Long updateUser(User user);

    Boolean containsId(Long userId);

    User getUserById(Long userId);

    ArrayList<User> getFriendsList(Long userId);

    void setFriendsList(Long userId, ArrayList<User> friendsList);


}
