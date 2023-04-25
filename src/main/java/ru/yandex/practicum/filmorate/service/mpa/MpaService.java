package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaDbStorage;

    public List<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

    public Mpa getMpaById(int mpaId) {
        return mpaDbStorage.getMpaById(mpaId);
    }
}