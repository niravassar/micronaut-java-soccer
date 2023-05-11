package com.example.soccer.domain;

import io.micronaut.serde.annotation.Serdeable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.AUTO;

@Serdeable
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "isbn", nullable = false)
    private String isbn;

    @ManyToOne
    private SoccerGame soccerGame;

    public Player() {}

    public Player(@NotNull String isbn,
                  @NotNull String name,
                  SoccerGame soccerGame) {
        this.isbn = isbn;
        this.name = name;
        this.soccerGame = soccerGame;
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public SoccerGame getSoccerGame() {
        return soccerGame;
    }

    public void setSoccerGame(SoccerGame soccerGame) {
        this.soccerGame = soccerGame;
    }
}
