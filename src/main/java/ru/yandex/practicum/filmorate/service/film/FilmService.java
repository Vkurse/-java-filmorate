package ru.yandex.practicum.filmorate.service.film;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmValidator filmValidator;

    public Film getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        filmValidator.validate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        filmValidator.validate(film);
        return filmStorage.updateFilm(film);
    }

    public Film like(Integer filmId, Integer userId) {
        return filmStorage.like(filmId, userId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        return filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getRating(count);
    }

    public LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy) {
        return filmStorage.filmsByDirector(directorId, sortBy);
    }
}