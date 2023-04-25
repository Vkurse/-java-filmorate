package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;


@Data
@Builder
public class User {
    private Integer id;

    @NotEmpty
    @Email(message = "Некорректный email.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "\\S*", message = "Логин содержит пробелы.")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent(message = "Некорректна указана дата рождения.")
    private LocalDate birthday;

    private Set<Integer> friends;
}
