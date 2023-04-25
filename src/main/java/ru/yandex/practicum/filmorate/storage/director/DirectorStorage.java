package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public interface DirectorStorage {

    /**
     * Метод для добавления режиссёра
     */
    Director addDirector(Director director);

    /**
     * Метод для получения всех режиссёров
     */
    List<Director> getDirectors();

    /**
     * Метод для получения режиссёра по id
     */
    Director getDirectorById(int id);

    /**
     * Метод для изменения данных режиссёра
     */
    void updateDirector(Director director);

    /**
     * Метод для получения режиссера
     */
    void deleteDirectorById(int id);

    /**
     * Метод для добавления режисёра к фильму
     */
    void addDirectorForCurrentFilm(Film film);

    /**
     * Метод для получения режиссёров фильма
     */
    LinkedHashSet<Director> getFilmDirectors(Film film);

    /**
     * Метод для добавления имени режиссёра в ответ запроса
     */
    void addDirectorNameToFilm(Film film);

    /**
     * Метод для обновления рижиссёров фильма
     */
    void updateDirectorsFilm(Film film);
}
