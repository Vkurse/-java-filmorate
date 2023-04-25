package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

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
        return userStorage.getUserById(userId);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
        return userStorage.getUserById(userId);
    }

    public List<User> getUserFriends(Integer userId) {
        return userStorage.getFriendsByUserId(userId);
    }

    public List<User> getCommonsFriends(Integer userId, Integer otherUserId) {
        return userStorage.getCommonsFriends(userId, otherUserId);
    }
}
