package com.example.soccer;

import com.example.soccer.domain.Player;
import com.example.soccer.domain.SoccerGame;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

public interface SoccerGameRepository {

    Optional<SoccerGame> findSoccerGameById(long id);

    SoccerGame saveSoccerGame(@NotBlank String name, int minPlayers, int maxPlayers);

    Player savePlayerToGame(PlayerSaveCommand playerSaveCommand);

    List<SoccerGame> findAllSoccerGames();

    List<Player> findAllPlayers();
}
