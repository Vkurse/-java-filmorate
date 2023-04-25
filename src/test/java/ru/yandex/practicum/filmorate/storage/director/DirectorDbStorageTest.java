package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Director;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorDbStorage directorDbStorage;
    private Director director;

    @BeforeEach
    void setUp() throws IOException {
        jdbcTemplate.update(Files.readString(Paths.get("src/test/resources/schema.sql")));
        jdbcTemplate.update(Files.readString(Paths.get("src/test/resources/data.sql")));
        director = Director.builder()
                .name("Режиссёр")
                .build();
    }

    @Test
    void addDirector() {
        assertEquals(director, directorDbStorage.addDirector(director));
    }

    @Test
    void addDirectorWithWrongId() {
        director.setId(9);
        assertEquals(1, directorDbStorage.addDirector(director).getId());
    }

    @Test
    void getDirectors() {
        directorDbStorage.addDirector(director);
        assertEquals(List.of(director), directorDbStorage.getDirectors());
    }

    @Test
    void getDirectorsEmptyList() {
        assertEquals(Collections.EMPTY_LIST, directorDbStorage.getDirectors());
    }

    @Test
    void getDirectorById() {
        directorDbStorage.addDirector(director);
        assertEquals(director, directorDbStorage.getDirectorById(director.getId()));
    }

    @Test
    void updateDirector() {
        directorDbStorage.addDirector(director);
        Director director1 = Director.builder()
                .id(1)
                .name("Изменено")
                .build();
        directorDbStorage.updateDirector(director1);
        assertEquals(director1, directorDbStorage.getDirectorById(1));
    }

    @Test
    void deleteDirectorById() {
        directorDbStorage.addDirector(director);
        directorDbStorage.deleteDirectorById(director.getId());
        assertEquals(Collections.EMPTY_LIST, directorDbStorage.getDirectors());
    }
}