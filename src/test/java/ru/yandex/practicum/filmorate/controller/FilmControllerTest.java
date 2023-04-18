package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

public class FilmControllerTest {


    FilmStorage filmStorage;
    FilmController controller;

    FilmService filmService;
    Film testFilm;

    @BeforeEach
    protected void init() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
        controller = new FilmController(filmStorage, filmService);
        testFilm = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание тестового фильма")
                .releaseDate(LocalDate.of(1999, 12, 27))
                .duration(87)
                .build();
    }

    @Test
    void createNewCorrectFilm_isOkTest() {
        controller.create(testFilm);
        Assertions.assertEquals(testFilm, filmStorage.getFilmById(1));
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
        try {
            controller.create(testFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals("Превышено количество символов в описании фильма.", e.getMessage());
        }
    }

    @Test
    void createFilm_RealiseDateInFuture_badRequestTest() {
        testFilm.setReleaseDate(LocalDate.of(2033, 4, 14));
        try {
            controller.create(testFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals("Некорректно указана дата релиза.", e.getMessage());
        }
    }

    @Test
    void createFilm_RealiseDateBeforeFirstFilmDate_badRequestTest() {
        testFilm.setReleaseDate(LocalDate.of(1833, 4, 14));
        try {
            controller.create(testFilm);
        } catch (ValidationException e) {
            Assertions.assertEquals("Некорректно указана дата релиза.", e.getMessage());
        }
    }

}