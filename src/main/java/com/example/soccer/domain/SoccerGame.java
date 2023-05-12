package com.example.soccer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.serde.annotation.Serdeable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.AUTO;

@Serdeable
@Entity
public class SoccerGame {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @JsonIgnore
    @OneToMany
    private Set<Player> playersTeamA = new HashSet<>();

    @JsonIgnore
    @OneToMany
    private Set<Player> playersTeamB = new HashSet<>();

    @NotNull
    @Column(name = "minPlayers", nullable = false)
    private int minPlayers;

    @NotNull
    @Column(name = "maxPlayers", nullable = false)
    private int maxPlayers;

    @NotNull
    @Column(name = "teamANumSubs", nullable = false)
    private int teamANumSubs;

    @NotNull
    @Column(name = "teamBNumSubs", nullable = false)
    private int teamBNumSubs;

    public SoccerGame() {}

    public SoccerGame(@NotNull String name, int minPlayers, int maxPlayers) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Player> getPlayersTeamA() {
        return playersTeamA;
    }

    public void setPlayersTeamA(Set<Player> playersTeamA) {
        this.playersTeamA = playersTeamA;
    }

    public Set<Player> getPlayersTeamB() {
        return playersTeamB;
    }

    public void setPlayersTeamB(Set<Player> playersTeamB) {
        this.playersTeamB = playersTeamB;
    }

    public int getMinPlayers() { return minPlayers; }

    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers;}

    public int getMaxPlayers() { return maxPlayers; }

    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers;}

    public int getTeamANumSubs() { return teamANumSubs; }

    public void setTeamANumSubs(int teamANumSubs) { this.teamANumSubs = teamANumSubs; }

    public int getTeamBNumSubs() { return teamBNumSubs; }

    public void setTeamBNumSubs(int teamBNumSubs) { this.teamBNumSubs = teamBNumSubs;}
}
