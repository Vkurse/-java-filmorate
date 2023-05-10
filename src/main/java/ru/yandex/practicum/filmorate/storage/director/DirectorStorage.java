package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;
import java.util.List;

public interface DirectorStorage {
    Director addDirector(Director director);

    List<Director> getDirectors();

    Director getDirectorById(int id);

    void updateDirector(Director director);

    void deleteDirectorById(int id);

    void addDirectorForCurrentFilm(Film film);

    LinkedHashSet<Director> getFilmDirectors(Film film);

    void addDirectorNameToFilm(Film film);

    void updateDirectorsFilm(Film film);
}