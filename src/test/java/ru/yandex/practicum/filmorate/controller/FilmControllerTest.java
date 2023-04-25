package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {

    private final JdbcTemplate jdbcTemplate;
    private final FilmController controller;
    private final DirectorController directorController;
    Film testFilm;

    @BeforeEach
    protected void init() throws IOException {
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
    }

    @Test
    void createNewCorrectFilm_isOkTest() {
        controller.create(testFilm);
        Assertions.assertEquals(testFilm, controller.getFilm(testFilm.getId()));
    }

    @Test
    void createFilm_NameIsBlank_badRequestTest() {
        testFilm.setName("");
        try {
            controller.create(testFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals("Некорректно указано название фильма.", e.getMessage());
        }
    }


    @Test
    void createFilm_IncorrectDescription_badRequestTest() {
        testFilm.setDescription("Размер описания значительно превышает двести символов, а может и не превышает " +
                "(надо посчитать). Нет, к сожалению размер описания фильма сейчас не превышает двести символов," +
                "но вот сейчас однозначно стал превышать двести символов!");
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> controller.create(testFilm));
        Assertions.assertEquals("Превышено количество символов в описании фильма.", exception.getMessage());

    }

    @Test
    void createFilm_RealiseDateInFuture_badRequestTest(){
        testFilm.setReleaseDate(LocalDate.of(2033, 4, 14));
        try {
            controller.create(testFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals("Некорректно указана дата релиза.", e.getMessage());
        }
    }

    @Test
    void createFilm_RealiseDateBeforeFirstFilmDate_badRequestTest(){
        testFilm.setReleaseDate(LocalDate.of(1833, 4, 14));
        try {
            controller.create(testFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals("Некорректно указана дата релиза.", e.getMessage());
        }
    }

    @Test
    void getFilmsByDirectorByYearNotCorrectSortBy() {
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
        directorController.addDirector(director);
        controller.create(testFilm);
        controller.create(film);
        controller.create(film2);

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> controller.filmsByDirector(director.getId(), "s"));
        Assertions.assertEquals("Ошибка в sortBy", exception.getMessage());
    }

    @Test
    void getFilmsByDirectorByYearNotCorrectDirector() {
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
        directorController.addDirector(director);
        controller.create(testFilm);
        controller.create(film);
        controller.create(film2);

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> controller.filmsByDirector(0, "year"));
        Assertions.assertEquals("Режиссёр с id = 0 не найден", exception.getMessage());
    }
}