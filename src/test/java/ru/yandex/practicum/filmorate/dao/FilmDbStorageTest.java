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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        List<Film> listFilms = filmDbStorage.getPopularFilms(5, -1, -1);
        Assertions.assertEquals(2, listFilms.size());
        List<Film> listFilms1 = filmDbStorage.getPopularFilms(5, 2, 1999);
        Assertions.assertEquals(1, listFilms1.size());
        Film film1 = listFilms1.get(0);
        Assertions.assertEquals("New film", film1.getName());
        List<Film> listFilms2 = filmDbStorage.getPopularFilms(5, -1, 1989);
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

    @Test
    @Sql(value = {"/test-schema-common-films.sql", "/test-data-common-films.sql"})
    void getCommonFilmsSortedByPopularityGeneralCase() {
        List<Film> common1 = filmDbStorage.getCommonFilms(1,2);
        assertThat(common1.get(0)).hasFieldOrPropertyWithValue("id", 2);
        filmDbStorage.like(1,3);
        filmDbStorage.like(1,4);
        filmDbStorage.like(1,5);
        List<Film> common2 = filmDbStorage.getCommonFilms(1,2);
        assertThat(common2.get(0)).hasFieldOrPropertyWithValue("id", 1);
        filmDbStorage.deleteLike(1, 5);
        List<Film> common3 = filmDbStorage.getCommonFilms(1,5);
        assertThat(common3.isEmpty()).isTrue();
    }

    @Test
    @Sql(value = {"/test-schema-common-films.sql", "/test-data-common-films.sql"})
    void getCommonFilmsSortedByPopularityEmptyList() {
        List<Film> common1 = filmDbStorage.getCommonFilms(1,5);
        assertThat(common1.isEmpty()).isTrue();
    }

    @Test
    @Sql(value = {"/test-schema-common-films.sql", "/test-data-common-films.sql"})
    void getCommonFilmsSortedByPopularityEqualLikesNumber() {
        filmDbStorage.deleteLike(2, 3);
        filmDbStorage.deleteLike(2, 4);
        List<Film> common1 = filmDbStorage.getCommonFilms(1,2);
        assertThat(common1.get(0)).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Sql(value = {"/schematest.sql", "/testdata-add-search.sql"})
    void searchFilmsByTitle() {
        List<Film> films = filmDbStorage.searchBy("док", "title");

        Assertions.assertEquals(films.size(), 3);
        Assertions.assertEquals(films.get(0).getId(), 1);
        Assertions.assertEquals(films.get(1).getId(), 2);
        Assertions.assertEquals(films.get(2).getId(), 3);
    }

    @Test
    @Sql(value = {"/schematest.sql", "/testdata-add-search.sql"})
    void searchFilmsByDirector() {
        List<Film> films = filmDbStorage.searchBy("ква", "director");

        Assertions.assertEquals(films.size(), 2);
        Assertions.assertEquals(films.get(0).getId(), 5);
        Assertions.assertEquals(films.get(1).getId(), 4);
    }

    @Test
    @Sql(value = {"/schematest.sql", "/testdata-add-search.sql"})
    void searchFilmsByTitleAndDirector() {
        List<Film> films = filmDbStorage.searchBy("к", "title,director");

        Assertions.assertEquals(films.size(), 5);
        Assertions.assertEquals(films.get(0).getId(), 1);
        Assertions.assertEquals(films.get(1).getId(), 2);
        Assertions.assertEquals(films.get(2).getId(), 5);
        Assertions.assertEquals(films.get(3).getId(), 4);
        Assertions.assertEquals(films.get(4).getId(), 3);
    }

    @Test
    @Sql(value = {"/test-schema-common-films.sql", "/test-data-recommended-films.sql"})
    void getRecommendedFilmsTest() {
        List<Film> recommendedFilms = filmDbStorage.getRecommendedFilms(1);
        Assertions.assertEquals(1, recommendedFilms.size());
        Assertions.assertEquals(filmDbStorage.getFilmById(2).getId(), recommendedFilms.get(0).getId());

        recommendedFilms = filmDbStorage.getRecommendedFilms(2);
        Assertions.assertEquals(1, recommendedFilms.size());
        Assertions.assertEquals(filmDbStorage.getFilmById(1).getId(), recommendedFilms.get(0).getId());
    }

    @Test
    @Sql(value = {"/test-schema-common-films.sql", "/test-data-recommended-films.sql"})
    void getRecommendedFilmsAddNewLikeTest() {
        filmDbStorage.like(3, 3);
        List<Film> recommendedFilms = filmDbStorage.getRecommendedFilms(3);

        Assertions.assertEquals(2, recommendedFilms.size());
        Assertions.assertEquals(filmDbStorage.getFilmById(1).getId(), recommendedFilms.get(0).getId());
        Assertions.assertEquals(filmDbStorage.getFilmById(2).getId(), recommendedFilms.get(1).getId());
    }
}