package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;


import javax.validation.constraints.NotNull;

@Data
@Builder
public class Mpa {
    @NotNull
    private Integer id;

    @NotNull
    private String name;
}
