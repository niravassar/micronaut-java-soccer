package com.example.soccer;

import com.example.soccer.domain.SoccerGame;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    Optional<SoccerGame> findById(long id);

    SoccerGame save(@NotBlank String name);

    SoccerGame saveWithException(@NotBlank String name);

    void deleteById(long id);

    List<SoccerGame> findAll(@NotNull SortingAndOrderArguments args);

    int update(long id, @NotBlank String name);
}
