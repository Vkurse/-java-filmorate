package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id);
    }

    public void updateDirector(Director director) {
        directorStorage.updateDirector(director);
    }

    public void deleteDirectorById(int id) {
        directorStorage.deleteDirectorById(id);
    }
}
