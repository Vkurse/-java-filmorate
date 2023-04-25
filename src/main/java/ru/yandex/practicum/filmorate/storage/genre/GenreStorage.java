package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    Genre getGenreForId(int id);

    List<Genre> findAll();

    Set<Genre> getGenreForCurrentFilm(int id);

    void addGenresForCurrentFilm(Film film);

    void updateGenresForCurrentFilm(Film film);
}
