package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final EventService eventService;

    public Film getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film like(Integer filmId, Integer userId) {
        Film film = filmStorage.like(filmId, userId);
        eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
        return film;
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.deleteLike(filmId, userId);
        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
        return film;
    }

    public List<Film> getBestFilmsOfGenreAndYear(int count, int genre, int year) {
        return filmStorage.getPopularFilms(count, genre, year);
    }

    public LinkedHashSet<Film> filmsByDirector(int directorId, String sortBy) {
        return filmStorage.filmsByDirector(directorId, sortBy);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getSearchResults(String query, String by) {
        return filmStorage.searchBy(query, by);
    }

    public Film deleteFilm(Integer id) {
        return filmStorage.deleteFilm(id);
    }
}
