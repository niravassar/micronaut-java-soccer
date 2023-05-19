package com.example.soccer.domain;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is a model of a game that has been through processing and is organized.
 * It has the players divided up into teams.
 */
@Serdeable
public class OrganizedSoccerGame {

    private SoccerGame soccerGame;
    private Date dateOrganized = new Date();
    private String gameInstructions;

    private List<Player> teamAPlayers = new ArrayList<Player>();
    private List<Player> teamBPlayers = new ArrayList<Player>();

    public OrganizedSoccerGame(@Nullable SoccerGame soccerGame) {
        this.soccerGame = soccerGame;
    }

    public String getGameInstructions() {
        return gameInstructions;
    }

    public void setGameInstructions(String gameInstructions) {
        this.gameInstructions = gameInstructions;
    }

    public void addTeamAPlayer(Player player) {
        this.teamAPlayers.add(player);
    }

    public void addTeamBPlayer(Player player) {
        this.teamBPlayers.add(player);
    }
    public List<Player> getTeamAPlayers() {
        return teamAPlayers;
    }

    public List<Player> getTeamBPlayers() {
        return teamBPlayers;
    }
}
