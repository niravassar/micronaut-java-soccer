package com.example.soccer;

import com.example.soccer.domain.OrganizedSoccerGame;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class SoccerGameService {

    public SoccerGameService() {
    }

    public List<OrganizedSoccerGame> organizeSoccerGames() {
        System.out.println("in the service now");
        return null;
    }
}
