package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director addDirector(Director director) {
        String sql = "INSERT INTO directors (name) " +
                "VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        int idKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
        director.setId(idKey);
        return director;
    }

    @Override
    public List<Director> getDirectors() {
        String sql = "SELECT * " +
                "FROM DIRECTORS";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director getDirectorById(int id) {
        String sql = "SELECT * " +
                "FROM DIRECTORS " +
                "WHERE DIRECTOR_ID = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeDirector(rs), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Режиссёр с id = {} не найден", id);
            throw new NotFoundException(String.format("Режиссёр с id = %d не найден", id));
        }
    }

    @Override
    public void updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS " +
                "SET NAME = ? " +
                "WHERE DIRECTOR_ID = ?";

        int update = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (update == 0) {
            log.error("Режиссёр с id = {} не найден", director.getId());
            throw new NotFoundException(String.format("Режиссёр с id = %d не найден", director.getId()));
        }
    }

    @Override
    public void deleteDirectorById(int id) {
        String sql = "DELETE FROM DIRECTORS " +
                "WHERE DIRECTOR_ID = ?";
        Director director = getDirectorById(id);
        jdbcTemplate.update(sql, id);
        log.info("Режиссёр {} удален", director);
    }

    @Override
    public void addDirectorForCurrentFilm(Film film) {
        if (Objects.isNull(film.getDirector())) {
            return;
        }

        try {
            film.getDirector().forEach(d -> {
                String sqlQuery = "INSERT INTO DIRECTOR_FILMS(FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
                jdbcTemplate.update(sqlQuery,
                        film.getId(),
                        d.getId());
            });
        } catch (DataIntegrityViolationException e) {
            log.error("Один из режисёров не найден: {}", film.getDirector());
            throw new NotFoundException("Один из режисёров не найден: " + film.getDirector());
        }
    }

    @Override
    public LinkedHashSet<Director> getFilmDirectors(Film film) {
            String sql = "SELECT d.DIRECTOR_ID, d.name FROM DIRECTOR_FILMS AS df LEFT JOIN DIRECTORS AS d ON df.DIRECTOR_ID = d.DIRECTOR_ID WHERE df.film_id = ?";
            Collection<Director> directors = jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), film.getId());
            return new LinkedHashSet<>(directors);
    }

    @Override
    public void addDirectorNameToFilm(Film film) {
        if (Objects.isNull(film.getDirector())) {
            return;
        }
        film.getDirector().forEach(d -> d.setName(getDirectorById(d.getId()).getName()));
    }

    @Override
    public void updateDirectorsFilm(Film film) {
        String sql = "DELETE FROM DIRECTOR_FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
        addDirectorForCurrentFilm(film);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("director_id");
        String name = rs.getString("name");
        return Director.builder()
                .id(id)
                .name(name)
                .build();
    }
}
