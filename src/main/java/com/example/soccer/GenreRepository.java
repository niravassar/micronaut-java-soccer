package com.example.soccer;

import com.example.soccer.domain.Soccer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    Optional<Soccer> findById(long id);

    Soccer save(@NotBlank String name);

    Soccer saveWithException(@NotBlank String name);

    void deleteById(long id);

    List<Soccer> findAll(@NotNull SortingAndOrderArguments args);

    int update(long id, @NotBlank String name);
}
