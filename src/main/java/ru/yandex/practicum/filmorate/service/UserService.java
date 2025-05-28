package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addToFriends(Long userId, Long friendId) {
        ArrayList<User> friendsList = userStorage.getFriendsList(userId);
        friendsList.add(userStorage.getUserById(friendId));
        userStorage.setFriendsList(userId, friendsList);

        friendsList = userStorage.getFriendsList(friendId);
        friendsList.add(userStorage.getUserById(userId));
        userStorage.setFriendsList(friendId, friendsList);
    }

    public void deleteFromFriends(Long userId, Long friendId) {
        ArrayList<User> friendsList = userStorage.getFriendsList(userId);
        friendsList.remove(userStorage.getUserById(friendId));
        userStorage.setFriendsList(userId, friendsList);

        friendsList = userStorage.getFriendsList(friendId);
        friendsList.remove(userStorage.getUserById(userId));
        userStorage.setFriendsList(friendId, friendsList);
    }

    public ArrayList<User> getCommonFriendsList(Long userId, Long friendId) {
        ArrayList<User> friendsList1 = userStorage.getFriendsList(userId);
        ArrayList<User> friendsList2 = userStorage.getFriendsList(friendId);

        friendsList1.retainAll(friendsList2);
        return friendsList1;
    }

}
