package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.*;

@DataJdbcTest
@Sql(value = {"/schematest.sql", "/datatest.sql"})
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public FilmDbStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    @Test
    public void getFilmByIdTest() {
        Film film1 = filmDbStorage.getFilmById(1);
        Assertions.assertEquals("Film Updated", film1.getName());
        Film film2 = filmDbStorage.getFilmById(2);
        Assertions.assertEquals("New film", film2.getName());
    }

    @Test
    public void getAllFilmsTest() {
        List<Film> listFilms = filmDbStorage.findAllFilms();
        Assertions.assertEquals(2, listFilms.size());
    }

    @Test
    public void getPopularFilmsTest() {
        List<Film> listFilms = filmDbStorage.getPopularFIlms(5, -1, -1);
        Assertions.assertEquals(2, listFilms.size());
        List<Film> listFilms1 = filmDbStorage.getPopularFIlms(5, 2, 1999);
        Assertions.assertEquals(1, listFilms1.size());
        Film film1 = listFilms1.get(0);
        Assertions.assertEquals("New film", film1.getName());
        List<Film> listFilms2 = filmDbStorage.getPopularFIlms(5, -1, 1989);
        Film film2 = listFilms2.get(0);
        Assertions.assertEquals("Film Updated", film2.getName());
        Assertions.assertEquals(1, listFilms2.size());
    }

    @Test
    public void getFilmsByDirectorSortByYearTest() {
        List<Film> films = filmDbStorage.findAllFilms();
        Assertions.assertEquals(2, films.size());
        LinkedHashSet<Director> directorsSet = new LinkedHashSet<>();
        directorsSet.add(filmDbStorage.getDirectorById(1));
        Assertions.assertEquals(directorsSet, filmDbStorage.getFilmDirectors(1));
        Assertions.assertEquals(directorsSet, filmDbStorage.getFilmDirectors(2));
        LinkedHashSet<Film> directorFilms = new LinkedHashSet<>();
        directorFilms.add(filmDbStorage.getFilmById(1));
        directorFilms.add(filmDbStorage.getFilmById(2));
        Assertions.assertEquals(directorFilms, filmDbStorage.filmsByDirector(1, "year"));

        Film film = Film.builder()
                .name("TestFilm")
                .description("TestDescription")
                .releaseDate(LocalDate.of(1980, 12, 12))
                .duration(90)
                .mpa(filmDbStorage.getMpaById(1))
                .genres(filmDbStorage.getGenre(1))
                .likes(Collections.emptySet())
                .directors(directorsSet)
                .build();
        filmDbStorage.addFilm(film);
        Assertions.assertEquals(directorsSet, filmDbStorage.getFilmDirectors(3));
        directorFilms.clear();
        directorFilms.add(filmDbStorage.getFilmById(3));
        directorFilms.add(filmDbStorage.getFilmById(1));
        directorFilms.add(filmDbStorage.getFilmById(2));
        Assertions.assertEquals(directorFilms, filmDbStorage.filmsByDirector(1, "year"));
    }

    @Test
    public void getFilmsByDirectorSortByErrorTest() {
        List<Film> films = filmDbStorage.findAllFilms();
        Assertions.assertEquals(2, films.size());
        LinkedHashSet<Director> directorsSet = new LinkedHashSet<>();
        directorsSet.add(filmDbStorage.getDirectorById(1));
        Assertions.assertEquals(directorsSet, filmDbStorage.getFilmDirectors(1));
        Assertions.assertEquals(directorsSet, filmDbStorage.getFilmDirectors(2));

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> filmDbStorage.filmsByDirector(1, "y"));
        Assertions.assertEquals("Ошибка в sortBy", exception.getMessage());
    }

    @Test
    public void getFilmsByDirectorDirectorNotFoundErrorTest() {
        List<Film> films = filmDbStorage.findAllFilms();
        Assertions.assertEquals(2, films.size());
        LinkedHashSet<Director> directorsSet = new LinkedHashSet<>();
        directorsSet.add(filmDbStorage.getDirectorById(1));
        Assertions.assertEquals(directorsSet, filmDbStorage.getFilmDirectors(1));
        Assertions.assertEquals(directorsSet, filmDbStorage.getFilmDirectors(2));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> filmDbStorage.filmsByDirector(2, "year"));
        Assertions.assertEquals("Режиссёр с id = 2 не найден", exception.getMessage());
    }

    @Test
    public void getFilmsByDirectorSortByLikesTest() {
        List<Film> films = filmDbStorage.findAllFilms();
        Assertions.assertEquals(2, films.size());
        LinkedHashSet<Director> directorsSet = new LinkedHashSet<>();
        directorsSet.add(filmDbStorage.getDirectorById(1));
        Assertions.assertEquals(directorsSet, filmDbStorage.getFilmDirectors(1));
        Assertions.assertEquals(directorsSet, filmDbStorage.getFilmDirectors(2));
        filmDbStorage.like(1, 1);
        filmDbStorage.like(1, 2);
        LinkedHashSet<Film> directorFilms = new LinkedHashSet<>();
        directorFilms.add(filmDbStorage.getFilmById(1));
        directorFilms.add(filmDbStorage.getFilmById(2));
        Assertions.assertEquals(directorFilms, filmDbStorage.filmsByDirector(1, "likes"));

        Film film = Film.builder()
                .name("TestFilm")
                .description("TestDescription")
                .releaseDate(LocalDate.of(1980, 12, 12))
                .duration(90)
                .mpa(filmDbStorage.getMpaById(1))
                .genres(filmDbStorage.getGenre(1))
                .likes(Collections.emptySet())
                .directors(directorsSet)
                .build();
        filmDbStorage.addFilm(film);
        Assertions.assertEquals(directorsSet, filmDbStorage.getFilmDirectors(3));
        filmDbStorage.like(3, 1);
        filmDbStorage.like(3, 2);
        filmDbStorage.like(3, 3);
        directorFilms.clear();
        directorFilms.add(filmDbStorage.getFilmById(3));
        directorFilms.add(filmDbStorage.getFilmById(1));
        directorFilms.add(filmDbStorage.getFilmById(2));
        Assertions.assertEquals(directorFilms, filmDbStorage.filmsByDirector(1, "likes"));
    }
}