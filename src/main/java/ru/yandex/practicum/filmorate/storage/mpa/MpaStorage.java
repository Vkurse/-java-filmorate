package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    Mpa getMpaById(int mpaId);

    List<Mpa> findAll();

    void addMpaToFilm(Film film);
}
