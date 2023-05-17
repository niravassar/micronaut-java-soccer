package com.example.soccer.domain;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.Date;

/**
 * This is a model of a game that has been through processing and is organized.
 * It has the players divided up into teams.
 */
@Serdeable
public class OrganizedSoccerGame {

    private SoccerGame soccerGame;
    private Date dateOrganized = new Date();

    public OrganizedSoccerGame(@Nullable SoccerGame soccerGame) {
        this.soccerGame = soccerGame;
    }
}
