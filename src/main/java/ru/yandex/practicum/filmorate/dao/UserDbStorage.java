package ru.yandex.practicum.filmorate.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(login, email, name, birthday)     " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET login = ?, email = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String FRIENDS_LIST_QUERY = "SELECT * FROM USERS WHERE ID in (SELECT USER_ID_2 FROM friends WHERE USER_ID_1 = ?)";
    private static final String DELETE_ALL_FRIENDS = "DELETE FROM friends WHERE USER_ID_1 = ?";
    private static final String ADD_FRIEND = "INSERT INTO friends(USER_ID_1, USER_ID_2)     " +
            "VALUES (?, ?)";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }


    @Override
    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        User user = findOne(FIND_BY_ID_QUERY, userId).isEmpty() ? null : findOne(FIND_BY_ID_QUERY, userId).get();
        if (user != null) {
            HashSet<Long> friendList = new HashSet<>();
            for (User friend : getFriendsList(userId)) {
                friendList.add(friend.getId());
            }
            user.setFriends(friendList);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }

    }

    @Override
    public Long createUser(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return id;
    }

    @Override
    public Long updateUser(User user) {
        update(
                UPDATE_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user.getId();
    }

    @Override
    public List<User> getFriendsList(Long userId) {
        return findMany(FRIENDS_LIST_QUERY, userId);
    }

    @Override
    public void setFriendsList(Long userId, List<User> friendsList) {
        delete(DELETE_ALL_FRIENDS, userId);
        for (User friend : friendsList) {
            insert(
                    ADD_FRIEND,
                    userId,
                    friend.getId()
            );
        }
    }


}
