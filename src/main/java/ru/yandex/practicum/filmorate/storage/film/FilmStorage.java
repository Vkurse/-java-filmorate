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

    List<Film> getPopularFilms(int count, int genre, int year);

    LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> searchBy(String query, String by);

    List<Film> getRecommendedFilms(int userId);

    Film deleteFilm(Integer id);
}
