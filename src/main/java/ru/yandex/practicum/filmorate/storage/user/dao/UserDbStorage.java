package ru.yandex.practicum.filmorate.storage.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User getUserById(Integer id) {
        String sqlQuery =
                "SELECT user_id, email, login, name, birthday " +
                        "FROM users " +
                        "WHERE user_id=?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Пользователь не найден.");
        }
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        while (rowSet.next()) {
            User user = User.builder()
                    .id(rowSet.getInt("user_id"))
                    .name(rowSet.getString("name"))
                    .login(rowSet.getString("login"))
                    .email(rowSet.getString("email"))
                    .birthday(Objects.requireNonNull(rowSet.getDate("birthday")).toLocalDate())
                    .build();
            users.add(user);
        }
        return users;
    }

    @Override
    public User addUser(User user) {
        validationUser(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(toMap(user)).intValue());
        log.info("Поступил запрос на добавление пользователя. Пользователь добавлен.");
        return user;
    }

    @Override
    public User updateUser(User user) {
        validationUser(user);

        String sqlQuery = "UPDATE users " +
                "SET email=?, login=?, name=?, birthday=? " +
                "WHERE user_id=?";

        int rowsCount = jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());

        if (rowsCount > 0) {
            log.info("Поступил запрос на изменения пользователя. Пользователь изменён.");
            return user;
        }
        throw new NotFoundException("Пользователь не найден.");
    }


    @Override
    public User addFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);

        try {
            getUserById(friendId);
        } catch (RuntimeException e) {
            throw new NotFoundException("Пользователь не найден.");
        }

        String sqlQuery =
                "INSERT " +
                        "INTO friends (user_id, friend_id) " +
                        "VALUES(?, ?)";

        jdbcTemplate.update(sqlQuery, userId, friendId);

        return user;
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);

        String sqlQuery =
                "DELETE " +
                        "FROM friends " +
                        "WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQuery, userId, friendId);

        return user;
    }

    @Override
    public List<User> getCommonsFriends(Integer id, Integer otherId) {
        String sqlQuery =
                "SELECT user_id, email, login, name, birthday " +
                        "FROM users " +
                        "WHERE user_id " +
                        "IN(SELECT friend_id " +
                        "FROM friends " +
                        "WHERE user_id = ?) " +
                        "AND user_id " +
                        "IN(SELECT friend_id " +
                        "FROM friends " +
                        "WHERE user_id = ?)";

        return new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, otherId));
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        String sqlQuery =
                "SELECT user_id, email, login, name, birthday " +
                        "FROM users " +
                        "WHERE user_id " +
                        "IN(SELECT friend_id " +
                        "FROM friends " +
                        "WHERE user_id=?)";

        return new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToUser, id));
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
        user.setFriends(getFriendsByUserId(user.getId()).stream().map(User::getId).collect(Collectors.toSet()));

        return user;
    }

    private void validationUser(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
