package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.like.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.UserDbStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;
    private final FilmService filmService;
    private final UserDbStorage userDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final DirectorStorage directorStorage;
    Film film;
    Film film2;
    User user;
    User user2;
    Film testFilm;


    @BeforeEach
    void setUp() throws IOException {
        jdbcTemplate.update(Files.readString(Paths.get("src/main/resources/schema.sql")));
        jdbcTemplate.update(Files.readString(Paths.get("src/main/resources/data.sql")));
        testFilm = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание тестового фильма")
                .releaseDate(LocalDate.of(1999, 12, 27))
                .duration(87)
                .likes(new HashSet<>())
                .mpa(Mpa.builder().id(1).name("G").build())
                .genres(new HashSet<>())
                .director(new HashSet<>())
                .build();
        film = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(1999, 8, 17))
                .duration(136)
                .build();
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());
        film.setMpa(Mpa.builder()
                .id(1)
                .name("NC-17")
                .build());

        film2 = Film.builder()
                .name("name2")
                .description("desc")
                .releaseDate(LocalDate.of(1999, 8, 17))
                .duration(136)
                .build();
        film2.setGenres(new HashSet<>());
        film2.setLikes(new HashSet<>());
        film2.setMpa(Mpa.builder()
                .id(1)
                .name("NC-17")
                .build());

        user = User.builder()
                .email("mail@mail.mail")
                .login("login")
                .birthday(LocalDate.of(1999, 8, 17))
                .build();
        user.setFriends(new HashSet<>());

        user2 = User.builder()
                .email("gmail@gmail.gmail")
                .login("nelogin")
                .birthday(LocalDate.of(2001, 6, 19))
                .build();
        user2.setFriends(new HashSet<>());
    }

    @Test
    void addFilmTest() {
        filmDbStorage.addFilm(film);
        film.setDirector(new HashSet<>());
        assertEquals(film, filmDbStorage.getFilmById(film.getId()));
    }

    @Test
    void updateFilmTest() {
        filmDbStorage.addFilm(film);
        film.setDirector(new HashSet<>());
        assertEquals(film, filmDbStorage.getFilmById(film.getId()));

        film.setName("updateName");
        filmDbStorage.updateFilm(film);
        assertEquals("updateName", filmDbStorage.getFilmById(film.getId()).getName());
    }

    @Test
    void likeAndDeleteLikeTest() {
        filmDbStorage.addFilm(film);
        userDbStorage.addUser(user);
        userDbStorage.addUser(user2);
        filmDbStorage.like(1, 1);
        filmDbStorage.like(1, 2);
        film.setLikes(likeDbStorage.getLikesForCurrentFilm(film.getId()));
        assertEquals(2, film.getLikes().size());

        filmDbStorage.deleteLike(1, 1);
        film.setLikes(likeDbStorage.getLikesForCurrentFilm(film.getId()));
        assertEquals(1, film.getLikes().size());
    }

    @Test
    void getRatingTest() {
        filmDbStorage.addFilm(film);
        userDbStorage.addUser(user);
        userDbStorage.addUser(user2);
        filmDbStorage.like(1, 1);
        filmDbStorage.like(1, 2);
        assertEquals(1, filmService.getTopFilms(1).get(0).getId());
    }

    @Test
    void getFilmsByDirectorByYearIsCorrect() {
        Director director = Director.builder()
                .name("Режиссёр")
                .build();
        HashSet<Director> directors = new HashSet<>();
        directors.add(director);
        testFilm.setDirector(directors);
        Film film = Film.builder()
                .name("Тестовый фильм 2")
                .description("Тестовое описание тестового фильма 2")
                .releaseDate(LocalDate.of(2000, 12, 27))
                .duration(87)
                .likes(new HashSet<>())
                .mpa(Mpa.builder().id(1).name("G").build())
                .genres(new HashSet<>())
                .director(directors)
                .build();
        Film film2 = Film.builder()
                .name("Тестовый фильм 3")
                .description("Тестовое описание тестового фильма 3")
                .releaseDate(LocalDate.of(2001, 12, 27))
                .duration(87)
                .likes(new HashSet<>())
                .mpa(Mpa.builder().id(1).name("G").build())
                .genres(new HashSet<>())
                .director(directors)
                .build();
        directorStorage.addDirector(director);
        filmDbStorage.addFilm(testFilm);
        filmDbStorage.addFilm(film);
        filmDbStorage.addFilm(film2);

        LinkedHashSet<Film> films = new LinkedHashSet<>();
        films.add(testFilm);
        films.add(film);
        films.add(film2);

        Assertions.assertEquals(films, filmDbStorage.filmsByDirector(director.getId(), "year"));
    }

    @Test
    void getFilmsByDirectorByLikesIsCorrect() {
        Director director = Director.builder()
                .name("Режиссёр")
                .build();
        HashSet<Director> directors = new HashSet<>();
        directors.add(director);
        testFilm.setDirector(directors);
        Film film = Film.builder()
                .name("Тестовый фильм 2")
                .description("Тестовое описание тестового фильма 2")
                .releaseDate(LocalDate.of(2000, 12, 27))
                .duration(87)
                .likes(new HashSet<>())
                .mpa(Mpa.builder().id(1).name("G").build())
                .genres(new HashSet<>())
                .director(directors)
                .build();
        Film film2 = Film.builder()
                .name("Тестовый фильм 3")
                .description("Тестовое описание тестового фильма 3")
                .releaseDate(LocalDate.of(2001, 12, 27))
                .duration(87)
                .likes(new HashSet<>())
                .mpa(Mpa.builder().id(1).name("G").build())
                .genres(new HashSet<>())
                .director(directors)
                .build();
        User user3 =  User.builder()
                .email("max@m.r")
                .login("2")
                .name("MaxMax")
                .birthday(LocalDate.of(2001, 12, 25))
                .build();
        directorStorage.addDirector(director);
        filmDbStorage.addFilm(testFilm);
        filmDbStorage.addFilm(film);
        filmDbStorage.addFilm(film2);
        userDbStorage.addUser(user);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);
        filmDbStorage.like(testFilm.getId(), user.getId());//testFilm 1 like
        filmDbStorage.like(film2.getId(), user2.getId());
        filmDbStorage.like(film2.getId(), user.getId());//film2 2 likes
        filmDbStorage.like(film.getId(), user3.getId());
        filmDbStorage.like(film.getId(), user.getId());
        filmDbStorage.like(film.getId(), user2.getId());//film 3 likes
        HashSet<Integer> likesTestFilm = new HashSet<>();
        likesTestFilm.add(1);
        testFilm.setLikes(likesTestFilm);
        HashSet<Integer> likesFilm2 = new HashSet<>();
        likesFilm2.add(2);
        likesFilm2.add(3);
        film2.setLikes(likesFilm2);
        HashSet<Integer> likesFilm = new HashSet<>();
        likesFilm.add(4);
        likesFilm.add(5);
        likesFilm.add(6);
        film.setLikes(likesFilm);

        LinkedHashSet<Film> films = new LinkedHashSet<>();
        films.add(film);
        films.add(film2);
        films.add(testFilm);

        Assertions.assertEquals(films, filmDbStorage.filmsByDirector(director.getId(), "likes"));
    }
}
