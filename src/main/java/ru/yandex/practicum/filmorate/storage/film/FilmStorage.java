package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;
import java.util.List;

public interface FilmStorage {
    Film getFilmById(Integer id);

    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film like(Integer filmId, Integer userId);

    Film deleteLike(Integer filmId, Integer userId);

    List<Film> getRating(int count);

    LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy);
}
