package ru.yandex.practicum.filmorate.service.event;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/test-schema-feed.sql", "/test-data-feed.sql"})
@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventServiceTest {

    @Autowired
    private final UserService userService;

    @Autowired
    private final FilmService filmService;

    @Test
    void addAndDeleteLikesTest() {
        filmService.like(1, 1);
        List<Event> events1 = userService.getFeed(1);
        Event event = events1.get(0);
        assertFieldsOfEvent(event, 1, 1, EventType.LIKE, EventOperation.ADD, 1);

        filmService.deleteLike(1, 1);
        List<Event> events2 = userService.getFeed(1);
        assertThat(events2.size()).isEqualTo(2);
        Event event1 = events2.get(0);
        Event event2 = events2.get(1);
        assertFieldsOfEvent(event1, 1, 1, EventType.LIKE, EventOperation.ADD, 1);
        assertFieldsOfEvent(event2, 2, 1, EventType.LIKE, EventOperation.REMOVE, 1);
    }

    @Test
    void addAndDeleteFriendEventTest() {
        userService.addFriend(1, 2);
        List<Event> events1 = userService.getFeed(1);
        assertThat(events1.size()).isEqualTo(1);
        Event event = events1.get(0);
        assertFieldsOfEvent(event, 1, 1, EventType.FRIEND, EventOperation.ADD, 2);

        userService.deleteFriend(1, 2);
        List<Event> events2 = userService.getFeed(1);
        assertThat(events2.size()).isEqualTo(2);
        Event event1 = events2.get(0);
        Event event2 = events2.get(1);
        assertFieldsOfEvent(event1, 1, 1, EventType.FRIEND, EventOperation.ADD, 2);
        assertFieldsOfEvent(event2, 2, 1, EventType.FRIEND, EventOperation.REMOVE, 2);
    }

    @Test
    void emptyFeedTest() {
        List<Event> events = userService.getFeed(1);
        assertThat(events.isEmpty()).isTrue();
    }

    @Test
    void addLikeAndFriendTest() {
        userService.addFriend(1, 2);
        filmService.like(1, 1);
        List<Event> events1 = userService.getFeed(1);
        assertThat(events1.size()).isEqualTo(2);
        Event event1 = events1.get(0);
        Event event2 = events1.get(1);
        assertFieldsOfEvent(event1, 1, 1, EventType.FRIEND, EventOperation.ADD, 2);
        assertFieldsOfEvent(event2, 2, 1, EventType.LIKE, EventOperation.ADD, 1);
    }

    private static void assertFieldsOfEvent(Event event, int eventId, int userId, EventType type, EventOperation operation, int entityId) {
        assertThat(event).hasFieldOrPropertyWithValue("eventId", eventId);
        assertThat(event).hasFieldOrPropertyWithValue("userId", userId);
        assertThat(event).hasFieldOrPropertyWithValue("eventType", type);
        assertThat(event).hasFieldOrPropertyWithValue("operation", operation);
        assertThat(event).hasFieldOrPropertyWithValue("entityId", entityId);
    }
}