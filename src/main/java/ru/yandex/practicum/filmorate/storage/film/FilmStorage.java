package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public interface FilmStorage {
    /**
     * Метод для получения списка фильмов
     */
    List<Film> findAllFilms();

    /**
     * Метод для добавления фильма
     */
    Film addFilm(Film film);

    /**
     * Метод для обновления фильма
     */
    Film updateFilm(Film film);

    /**
     * Метод получения фильма по id
     */
    Film getFilmById(int id);

    /**
     * Метод для присвоения лайка фильму
     */
    Film like(int filmId, int userId);

    /**
     * Метод для удаления лайка с фильма
     */
    Film deleteLike(int filmId, int userId);

    /**
     * Метод для получения списка популярных фильмов
     */
    List<Film> getRating(int count);

    LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy);
}
