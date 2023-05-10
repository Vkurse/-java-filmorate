package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info("Пришел /POST запрос на добавление режиссёра: {}", director);
        return directorService.addDirector(director);
    }

    @GetMapping
    public List<Director> getDirectors() {
        log.info("Пришел /GET запрос на получение всех режиссёров");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("Получен /GET запрос на получение режиссёра с id = {}", id);
        return directorService.getDirectorById(id);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        log.info("Получен /PUT запрос на изменение данных режиссёра с id = {}", director.getId());
        directorService.updateDirector(director);
        return directorService.getDirectorById(director.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable int id) {
        log.info("Получен /DELETE запрос на удаление режиссёра с id = {}", id);
        directorService.deleteDirectorById(id);
    }
}