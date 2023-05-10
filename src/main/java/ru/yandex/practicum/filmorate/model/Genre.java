package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = "name")
public class Genre implements Comparable<Genre> {

    @NotNull
    private Integer id;
    @NotNull
    private String name;

    public int compareTo(Genre genre) {
        return (genre.getId().compareTo(this.getId()));
    }
}
