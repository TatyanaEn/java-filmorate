package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        return userService.createUser(user);
    }


    @PutMapping
    public User update(@RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addToFriends(@PathVariable("userId") Long userId,
                             @PathVariable("friendId") Long friendId) {

        return userService.addToFriends(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable("userId") Long userId,
                                  @PathVariable("friendId") Long friendId) {

        userService.deleteFromFriends(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public ArrayList<User> getFriends(@PathVariable("userId") Long userId) {
        return userService.getFriendsList(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public ArrayList<User> getCommonFriends(@PathVariable("userId") Long userId,
                                            @PathVariable("otherId") Long otherId) {
        return userService.getCommonFriendsList(userId, otherId);
    }


}
