package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;


    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Поступил запрос на создание пользователя.");
        return userStorage.addUser(user);
    }

    @PutMapping
    public User changeUser(@Valid @RequestBody User user) {
        log.info("Поступил запрос на обновление пользователя.");
        return userStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable String id, @PathVariable String friendId) {
        log.info("Поступил запрос на добавления в друзья.");
        return userService.addFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Поступил запрос на получение списка пользователей.");
        return userStorage.findAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        log.info("Поступил запрос на получение пользователя по id.");
        return userStorage.getUserById(Integer.parseInt(id));
    }


    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable String id) {
        log.info("Поступил запрос на получение списка друзей.");
        return userService.getUserFriends(Integer.parseInt(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable String id, @PathVariable String otherId) {
        log.info("Поступил запрос на получения списка общих друзей.");
        return userService.getMutualFriends(Integer.parseInt(id), Integer.parseInt(otherId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable String id, @PathVariable String friendId) {
        log.info("Поступил запрос на удаление из друзей.");
        return userService.deleteFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

}


