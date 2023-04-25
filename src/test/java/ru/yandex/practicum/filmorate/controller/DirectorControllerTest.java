package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorControllerTest {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorController directorController;
    private Director director;

    @BeforeEach
    void setUp() throws IOException {
        jdbcTemplate.update(Files.readString(Paths.get("src/main/resources/schema.sql")));
        jdbcTemplate.update(Files.readString(Paths.get("src/main/resources/data.sql")));
        director = Director.builder()
                .name("Режиссёр")
                .build();
    }

    @Test
    void getDirectorByIdWrongId() {
        directorController.addDirector(director);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> directorController.getDirectorById(0));
        assertEquals("Режиссёр с id = 0 не найден",exception.getMessage());
    }

    @Test
    void updateDirectorWithWrongId() {
        directorController.addDirector(director);
        Director director1 = Director.builder()
                .id(0)
                .name("Изменено")
                .build();

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> directorController.updateDirector(director1));
        assertEquals("Режиссёр с id = 0 не найден",exception.getMessage());
    }

    @Test
    void deleteDirectorByIdWrongId() {
        directorController.addDirector(director);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> directorController.deleteDirectorById(0));
        assertEquals("Режиссёр с id = 0 не найден",exception.getMessage());
    }
}