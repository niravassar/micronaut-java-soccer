package com.example.soccer;

import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.NotBlank;

@Serdeable
public class SoccerGameSaveCommand {

    @NotBlank
    private String name;

    private int minPlayers;
    private int maxPlayers;
    private int teamANumSubs;
    private int teamBNumSubs;

    public SoccerGameSaveCommand(String name, int minPlayers, int maxPlayers) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getTeamANumSubs() {
        return teamANumSubs;
    }

    public void setTeamANumSubs(int teamANumSubs) {
        this.teamANumSubs = teamANumSubs;
    }

    public int getTeamBNumSubs() {
        return teamBNumSubs;
    }

    public void setTeamBNumSubs(int teamBNumSubs) {
        this.teamBNumSubs = teamBNumSubs;
    }
}
