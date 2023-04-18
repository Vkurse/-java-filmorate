package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;
    User testUser;
    User testFriend;
    User mutualFriend;

    @BeforeEach
    public void setup() {
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM friends");
        testUser = User.builder()
                .email("mail@mail.mail")
                .login("login")
                .birthday(LocalDate.of(1999, 8, 17))
                .build();
        testUser.setFriends(new HashSet<>());

        testFriend = User.builder()
                .email("gmail@gmail.gmail")
                .login("nelogin")
                .birthday(LocalDate.of(1995, 7, 14))
                .build();
        testFriend.setFriends(new HashSet<>());

        mutualFriend = User.builder()
                .email("mutual@mutual.mutual")
                .login("mutual")
                .birthday(LocalDate.of(2001, 1, 11))
                .build();
        mutualFriend.setFriends(new HashSet<>());
    }

    @Test
    void shouldCreateAndUpdateAndGetUser() {
        userDbStorage.addUser(testUser);
        assertEquals(testUser, userDbStorage.getUserById(testUser.getId()));
        assertEquals(testUser.getLogin(), userDbStorage.getUserById(testUser.getId()).getName());

        testUser.setEmail("lol@lol.lol");
        userDbStorage.updateUser(testUser);
        assertEquals(testUser, userDbStorage.getUserById(testUser.getId()));

        assertEquals(1, userDbStorage.findAllUsers().size());
        assertEquals(testUser, userDbStorage.getUserById(testUser.getId()));
    }

    @Test
    void shouldAddAndDeleteFriends() {
        userDbStorage.addUser(testUser);
        userDbStorage.addUser(testFriend);
        userDbStorage.addFriend(testUser.getId(), testFriend.getId());
        assertEquals(1, userDbStorage.getFriendsByUserId(testUser.getId()).size());
        assertEquals(0, userDbStorage.getFriendsByUserId(testFriend.getId()).size());

        userDbStorage.deleteFriend(testUser.getId(), testFriend.getId());
        assertEquals(0, userDbStorage.getFriendsByUserId(testUser.getId()).size());
        assertEquals(0, userDbStorage.getFriendsByUserId(testFriend.getId()).size());
    }

    @Test
    void shouldGetMutualFriends() {
        userDbStorage.addUser(testUser);
        userDbStorage.addUser(testFriend);
        userDbStorage.addUser(mutualFriend);
        userDbStorage.addFriend(testUser.getId(), mutualFriend.getId());
        userDbStorage.addFriend(testFriend.getId(), mutualFriend.getId());
        assertSame(userDbStorage.getMutualFriends(testUser.getId(), testFriend.getId()).get(0).getId(), mutualFriend.getId());
    }

}