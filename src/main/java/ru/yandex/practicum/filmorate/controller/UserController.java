package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Integer userId) {
        log.info("Поступил запрос на получение пользователя по id.");
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Поступил запрос на получение списка пользователей.");
        return userService.findAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Поступил запрос на создание пользователя.");
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Поступил запрос на обновление пользователя.");
        return userService.updateUser(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        log.info("Поступил запрос на добавления в друзья.");
        return userService.addFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable Integer userId) {
        log.info("Поступил запрос на получение списка друзей.");
        return userService.getUserFriends(userId);
    }

    @GetMapping("/{firstUserId}/friends/common/{secondUserId}")
    public List<User> getCommonsFriends(@PathVariable Integer firstUserId, @PathVariable Integer secondUserId) {
        log.info("Поступил запрос на получения списка общих друзей.");
        return userService.getCommonsFriends(firstUserId, secondUserId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Поступил запрос на удаление из друзей.");
        userService.deleteFriend(id, friendId);
    }

    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable Integer id) {
        log.info("Поступил запрос на удаление пользователя c id " + id);
        return userService.deleteUser(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable int id) {
        return userService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getFilmRecommendations(@PathVariable Integer id) {
        log.info("Поступил запрос на получение рекомендаций по фильмам.");
        return userService.getFilmRecommendations(id);
    }
}
