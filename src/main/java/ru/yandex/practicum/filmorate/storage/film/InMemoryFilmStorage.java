package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    FilmValidator filmValidator = new FilmValidator();
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int idForFilm = 0;

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        filmValidator.validate(film);
        film.setLikes(new HashSet<>());
        film.setId(getIdForFilm());
        films.put(film.getId(), film);
        log.info("Поступил запрос на добавление фильма. Фильм добавлен");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.get(film.getId()) != null) {
            filmValidator.validate(film);
            film.setLikes(new HashSet<>());
            films.put(film.getId(), film);
            log.info("Поступил запрос на изменения фильма. Фильм изменён.");
        } else {
            log.error("Поступил запрос на изменения фильма. Фильм не найден.");
            throw new NotFoundException("Film not found.");
        }
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else throw new NotFoundException("Film not found.");
    }

    @Override
    public Film like(int filmId, int userId) {
        getFilmById(filmId).getLikes().add(userId);
        return getFilmById(filmId);
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        if (getFilmById(filmId).getLikes().contains(userId)) {
            getFilmById(filmId).getLikes().remove(userId);
        } else {
            throw new NotFoundException("Пользователь не ставил оценку данному фильму.");
        }
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getRating(int count) {
        return findAllFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count).collect(Collectors.toList());
    }

    @Override
    public LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy) {
        return null;
    }

    private int getIdForFilm() {
        return ++idForFilm;
    }
}
