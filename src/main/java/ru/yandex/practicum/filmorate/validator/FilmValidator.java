package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
public class FilmValidator {
    public void validate(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))
                || film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректно указана дата релиза.");
        }
        if (film.getName().isEmpty()) {
            throw new ValidationException("Некорректно указано название фильма.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Превышено количество символов в описании фильма.");
        }
    }
}
