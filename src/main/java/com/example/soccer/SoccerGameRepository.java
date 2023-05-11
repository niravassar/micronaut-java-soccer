package com.example.soccer;

import com.example.soccer.domain.SoccerGame;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

public interface SoccerGameRepository {

    Optional<SoccerGame> findById(long id);

    SoccerGame save(@NotBlank String name, int minPlayers, int maxPlayers);

    List<SoccerGame> findAll();
}
