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
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Player> playerPool = new HashSet<>();

    @NotNull
    @Column(name = "minPlayers")
    private int minPlayers;

    @NotNull
    @Column(name = "maxPlayers")
    private int maxPlayers;

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

    public int getMinPlayers() { return minPlayers; }

    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers;}

    public int getMaxPlayers() { return maxPlayers; }

    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers;}

    public Set<Player> getPlayerPool() {return playerPool;}

    public void setPlayerPool(Set<Player> playerPool) {this.playerPool = playerPool;}

    public void addPlayerToPlayerPool(Player player) {
        this.playerPool.add(player);
    }
}
