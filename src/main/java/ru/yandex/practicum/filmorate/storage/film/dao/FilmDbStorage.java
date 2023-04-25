package ru.yandex.practicum.filmorate.storage.film.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final MpaDbStorage mpaDbStorage;
    private final LikeStorage likeDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final DirectorStorage directorStorage;

    @Override
    public List<Film> findAllFilms() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT film_id, name, description, release_date," +
                " duration, rating_mpa_id FROM films");
        while (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getInt("film_id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(mpaDbStorage.getMpa(filmRows.getInt("rating_mpa_id")))
                    .build();
            film.setGenres(genreDbStorage.getGenreForCurrentFilm(film.getId()));
            film.setLikes(likeDbStorage.getLikesForCurrentFilm(film.getId()));
            film.setDirector(directorStorage.getFilmDirectors(film));

            films.add(film);
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue());
        mpaDbStorage.addMpaToFilm(film);
        genreDbStorage.addGenreNameToFilm(film);
        genreDbStorage.addGenresForCurrentFilm(film);
        directorStorage.addDirectorForCurrentFilm(film);
        directorStorage.addDirectorNameToFilm(film);

        log.info("Поступил запрос на добавление фильма. Фильм добавлен.");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name=?, description=?, release_date=?, duration=?, rating_mpa_id=? WHERE film_id=?";
        int rowsCount = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        mpaDbStorage.addMpaToFilm(film);
        genreDbStorage.updateGenresForCurrentFilm(film);
        genreDbStorage.addGenreNameToFilm(film);
        directorStorage.updateDirectorsFilm(film);
        directorStorage.addDirectorNameToFilm(film);
        film.setGenres(genreDbStorage.getGenreForCurrentFilm(film.getId()));
        if (rowsCount > 0) {
            return film;
        }
        throw new NotFoundException("Фильм не найден.");
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT film_id, name, description, release_date, duration, rating_mpa_id " +
                "FROM films WHERE film_id=?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Фильм не найден.");
        }
    }

    @Override
    public Film like(int filmId, int userId) {
        Film film = getFilmById(filmId);
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return film;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        Film film = getFilmById(filmId);
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return film;
    }

    @Override
    public List<Film> getRating(int count) {
        String sqlQuery = "SELECT films.*, COUNT(l.film_id) as count FROM films\n" +
                "LEFT JOIN likes l ON films.film_id=l.film_id\n" +
                "GROUP BY films.film_id\n" +
                "ORDER BY count DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy) {
        directorStorage.getDirectorById(directorId);
        String sql;
        if (sortBy.equals("year")) {
            sql =   "SELECT f.* " +
                    "FROM DIRECTOR_FILMS AS df " +
                    "JOIN FILMS AS f ON df.FILM_ID = f.FILM_ID " +
                    "WHERE DIRECTOR_ID = ? " +
                    "GROUP BY f.FILM_ID, f.RELEASE_DATE " +
                    "ORDER BY f.RELEASE_DATE";
        } else if (sortBy.equals("likes")) {
            sql = "SELECT f.* " +
                    "FROM DIRECTOR_FILMS AS df " +
                    "JOIN FILMS AS f ON df.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN LIKES AS l On f.FILM_ID = l.FILM_ID " +
                    "WHERE DIRECTOR_ID = ? " +
                    "GROUP BY f.FILM_ID, l.FILM_ID IN (SELECT FILM_ID FROM LIKES) " +
                    "ORDER BY COUNT(l.FILM_ID) DESC";
        } else {
            log.error("Ошибка в sortBy");
            throw new ValidationException("Ошибка в sortBy");
        }
        Collection<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
        return new LinkedHashSet<>(films);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpaDbStorage.getMpa(resultSet.getInt("rating_mpa_id")))
                .build();
        film.setLikes(likeDbStorage.getLikesForCurrentFilm(film.getId()));
        film.setGenres(genreDbStorage.getGenreForCurrentFilm(film.getId()));
        film.setDirector(directorStorage.getFilmDirectors(film));
        return film;
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rating_mpa_id", film.getMpa().getId());
        return values;
    }
}
