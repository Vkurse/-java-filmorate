package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.film.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping( "/directors")
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info("Пришел /POST запрос на добавление режиссёра: {}", director);
        Director director1 = directorService.addDirector(director);
        log.info("Ответ отправлен: {}", director1);
        return director1;
    }

    @GetMapping
    public List<Director> getDirectors() {
        log.info("Пришел /GET запрос на получение всех режиссёров");
        List<Director> directors = directorService.getDirectors();
        log.info("Ответ отправлен: {}", directors);
        return directors;
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("Получен /GET запрос на получение режиссёра с id = {}", id);
        Director director = directorService.getDirectorById(id);
        log.info("Ответ отправлен: {}", director);
        return director;
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        log.info("Получен /PUT запрос на изменение данных режиссёра с id = {}", director.getId());
        directorService.updateDirector(director);
        Director director1 = directorService.getDirectorById(director.getId());
        log.info("Ответ отправлен: {}", director1);
        return director1;
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable int id) {
        log.info("Получен /DELETE запрос на на удаление режиссёра с id = {}", id);
        directorService.deleteDirectorById(id);
    }
}
