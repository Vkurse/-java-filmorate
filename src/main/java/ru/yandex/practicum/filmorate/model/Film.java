package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;


@Data
@Builder
public class Film {

    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NotNull
    @ReleaseDate(message = "Некорректна указана дата релиза.")
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Set<Integer> likes;

    @NotNull
    private Mpa mpa;

    private Set<Genre> genres;

    private Set<Director> director;

}
