package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.director.dao.DirectorDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Sql(value = {"/schematest.sql", "/datatest.sql"})
class DirectorDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorDbStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        directorStorage = new DirectorDbStorage(jdbcTemplate);
    }

    @Test
    void addDirector() {
        Director director = Director.builder()
                .name("SecondDirector")
                .build();

        directorStorage.addDirector(director);
        assertEquals(director, directorStorage.getDirectorById(2));
    }

    @Test
    void getDirectors() {
        Director director = Director.builder()
                .id(1)
                .name("firstDirector")
                .build();
        Collection<Director> directors = new ArrayList<>();
        directors.add(director);

        assertEquals(directors, directorStorage.getDirectors());
    }

    @Test
    void getDirectorsEmptyList() {
        directorStorage.deleteDirectorById(1);

        assertEquals(Collections.emptyList(), directorStorage.getDirectors());
    }


    @Test
    void getDirectorById() {
        Director director = Director.builder()
                .id(1)
                .name("firstDirector")
                .build();

        assertEquals(director, directorStorage.getDirectorById(1));
    }

    @Test
    void getDirectorByIdErrorId() {
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> directorStorage.getDirectorById(2));

        assertEquals("Режиссёр с id = 2 не найден", exception.getMessage());
    }

    @Test
    void updateDirector() {
        Director director = Director.builder()
                .id(1)
                .name("UpdatedDirector")
                .build();
        directorStorage.updateDirector(director);

        assertEquals(director, directorStorage.getDirectorById(1));
    }

    @Test
    void updateDirectorErrorId() {
        Director director = Director.builder()
                .id(2)
                .name("UpdatedDirector")
                .build();
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> directorStorage.updateDirector(director));

        assertEquals("Режиссёр с id = 2 не найден", exception.getMessage());
        director.setId(1);
        director.setName("firstDirector");

        assertEquals(director, directorStorage.getDirectorById(1));
    }

    @Test
    void deleteDirectorById() {
        Director director = Director.builder()
                .name("SecondDirector")
                .build();

        directorStorage.addDirector(director);
        directorStorage.deleteDirectorById(1);
        Collection<Director> directors = new ArrayList<>();
        directors.add(director);

        assertEquals(directors, directorStorage.getDirectors());
    }

    @Test
    void deleteDirectorByIdErrorId() {
        Director director = Director.builder()
                .name("SecondDirector")
                .build();

        directorStorage.addDirector(director);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> directorStorage.deleteDirectorById(3));

        assertEquals("Режиссёр с id = 3 не найден", exception.getMessage());

        Collection<Director> directors = new ArrayList<>();
        directors.add(directorStorage.getDirectorById(1));
        directors.add(director);

        assertEquals(directors, directorStorage.getDirectors());
    }
}