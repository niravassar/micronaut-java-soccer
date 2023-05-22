package com.example.soccer.domain;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public void createGameInstructions() {
        StringBuilder sb = new StringBuilder();
        String dateSentence = "This game is titled `" + this.soccerGame.getName() + "` and will take place on " + dateOrganized + ".";
        String maxPlayersSentence = "The game needs a min of " + this.soccerGame.getMinPlayers() + " and a max of " + this.soccerGame.getMaxPlayers() +". They are split into two teams.";
        String teamAPlayers = this.teamAPlayers.stream().map(player -> player.toString()).collect(Collectors.toList()).toString();
        String teamBPlayers = this.teamBPlayers.stream().map(player -> player.toString()).collect(Collectors.toList()).toString();
        String playersASentence = "Team A will have " + teamAPlayers + ".";
        String playersBSentence = "Team B will have " + teamBPlayers + ".";

        sb.append(dateSentence).append(" ").
                append(maxPlayersSentence).append(" ").
                append(playersASentence).append(" ").
                append(playersBSentence);

        this.gameInstructions = sb.toString();
    }

    /*******************************************************************************************/

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
        return teamAPlayers.stream().sorted().collect(Collectors.toList());
    }

    public List<Player> getTeamBPlayers() {
        return teamBPlayers.stream().sorted().collect(Collectors.toList());
    }

    public void setTeamAPlayers(List<Player> teamAPlayers) {
        this.teamAPlayers = teamAPlayers;
    }

    public void setTeamBPlayers(List<Player> teamBPlayers) {
        this.teamBPlayers = teamBPlayers;
    }

    public SoccerGame getSoccerGame() {
        return soccerGame;
    }

    public Date getDateOrganized() {
        return dateOrganized;
    }

    public void setDateOrganized(Date dateOrganized) {
        this.dateOrganized = dateOrganized;
    }
}
