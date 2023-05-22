package com.example.soccer;

import com.example.soccer.domain.OrganizedSoccerGame;
import com.example.soccer.domain.Player;
import com.example.soccer.domain.SoccerGame;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class SoccerGameService {

    private SoccerGameRepository soccerGameRepository;

    public SoccerGameService(SoccerGameRepository soccerGameRepository) {
        this.soccerGameRepository = soccerGameRepository;
    }

    @ReadOnly
    public List<OrganizedSoccerGame> organizeSoccerGames() {
        List<OrganizedSoccerGame> organizedSoccerGames = new ArrayList<OrganizedSoccerGame>();
        List<SoccerGame> soccerGames = soccerGameRepository.findAllSoccerGames();

        for (SoccerGame soccerGame : soccerGames) {

            OrganizedSoccerGame organizedSoccerGame = new OrganizedSoccerGame(soccerGame);

            Set<Player> playerPool = soccerGame.getPlayerPool();
            if (playerPool.size() < soccerGame.getMinPlayers()) {
                organizedSoccerGame.setGameInstructions("This game cannot be played because it has only " + playerPool.size()
                        + " players and we need minimum " + soccerGame.getMinPlayers() + " players.");
            } else {
                List<Player> sortedPlayersByAge = playerPool.stream().sorted(Comparator.comparing(Player::getAge)).collect(Collectors.toList());
                Queue<Player> playersQueue = new PriorityQueue<Player>();
                playersQueue.addAll(sortedPlayersByAge);

                boolean organizeTeamA = true;

                while (!playersQueue.isEmpty()) {
                    if (organizeTeamA) {
                        organizedSoccerGame.addTeamAPlayer(playersQueue.remove());
                    } else {
                        organizedSoccerGame.addTeamBPlayer(playersQueue.remove());
                    }
                    // toggle it
                    organizeTeamA = !organizeTeamA;
                    // sort again
                    sortedPlayersByAge = playersQueue.stream().sorted(Comparator.comparing(Player::getAge)).collect(Collectors.toList());
                    playersQueue.clear();
                    playersQueue.addAll(sortedPlayersByAge);
                }
            }

            organizedSoccerGames.add(organizedSoccerGame);
        }

        return organizedSoccerGames;
    }
}
