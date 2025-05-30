package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
    }

    @Override
    public Collection<User> findAll() {
        return users.values().stream()
                .map(user -> User.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .login(user.getLogin())
                        .email(user.getEmail())
                        .birthday(user.getBirthday())
                        .friends(user.getFriends())
                        .build())
                .toList();
    }

    @Override
    public Long createUser(User user) {
        User newUser = User.builder()
                .id(getNextId())
                .name(user.getName())
                .login(user.getLogin())
                .email(user.getEmail())
                .birthday(user.getBirthday())
                .friends(user.getFriends())
                .build();
        users.put(newUser.getId(), newUser);

        return newUser.getId();
    }

    @Override
    public Long updateUser(User newUser) {
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
        users.put(oldUser.getId(), oldUser);
        return oldUser.getId();
    }

    @Override
    public Boolean containsId(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public User getUserById(Long userId) {
        User userFS = users.get(userId);
        if (userFS == null)
            return null;
        return User.builder()
                .id(userId)
                .name(userFS.getName())
                .login(userFS.getLogin())
                .email(userFS.getEmail())
                .birthday(userFS.getBirthday())
                .friends(userFS.getFriends())
                .build();
    }

    @Override
    public ArrayList<User> getFriendsList(Long userId) {
        ArrayList<User> friendsList = new ArrayList<>();

        if (users.get(userId).getFriends() != null) {
            for (Long friendId : users.get(userId).getFriends()) {
                friendsList.add(users.get(friendId));
            }
        }
        return friendsList;
    }

    @Override
    public void setFriendsList(Long userId, ArrayList<User> friendsList) {
        Set<Long> friendsIdList = new HashSet<>();
        if (friendsList != null) {
            for (User friend : friendsList) {
                friendsIdList.add(friend.getId());
            }
        }
        users.get(userId).setFriends(friendsIdList);
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
}
