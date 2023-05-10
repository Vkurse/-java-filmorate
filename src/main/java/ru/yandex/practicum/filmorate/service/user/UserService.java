package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final EventService eventService;
    private final FilmStorage filmStorage;

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User addFriend(Integer userId, Integer friendId) {
        userStorage.addFriend(userId, friendId);
        eventService.createEvent(userId, EventType.FRIEND, EventOperation.ADD, friendId);
        return userStorage.getUserById(userId);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
        eventService.createEvent(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
        return userStorage.getUserById(userId);
    }

    public List<User> getUserFriends(Integer userId) {
        return userStorage.getFriendsByUserId(userId);
    }

    public List<User> getCommonsFriends(Integer userId, Integer otherUserId) {
        return userStorage.getCommonsFriends(userId, otherUserId);
    }

    public List<Event> getFeed(Integer userId) {
        getUserById(userId);
        return eventService.getFeed(userId);
    }

    public List<Film> getFilmRecommendations(Integer id) {
        getUserById(id);
        return filmStorage.getRecommendedFilms(id);
    }

    public User deleteUser(Integer id) {
        return userStorage.deleteUser(id);
    }
}
