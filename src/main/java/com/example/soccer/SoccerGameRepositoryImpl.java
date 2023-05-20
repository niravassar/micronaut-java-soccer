package com.example.soccer;

import com.example.soccer.domain.Player;
import com.example.soccer.domain.SoccerGame;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@Singleton
public class SoccerGameRepositoryImpl implements SoccerGameRepository {

    private final EntityManager entityManager;

    public SoccerGameRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @ReadOnly
    public Optional<SoccerGame> findSoccerGameById(long id) {
        return Optional.ofNullable(entityManager.find(SoccerGame.class, id));
    }

    @Override
    @Transactional
    public SoccerGame saveSoccerGame(@NotBlank String name, int minPlayers, int maxPlayers) {
        SoccerGame soccerGame = new SoccerGame(name, minPlayers, maxPlayers);
        entityManager.persist(soccerGame);
        return soccerGame;
    }

    @Override
    @Transactional
    public Player savePlayerToGame(PlayerSaveCommand playerSaveCommand) {
        Optional<SoccerGame> soccerGame = this.findSoccerGameById(playerSaveCommand.getSoccerGameId());
        Player player = new Player(playerSaveCommand.getName(), playerSaveCommand.getAge());
        soccerGame.ifPresent(sg -> {
            if(sg.getPlayerPool().size() < sg.getMaxPlayers()) {
                sg.addPlayerToPlayerPool(player);
            } else {
                throw new RuntimeException("soccer game " + sg.getName() + " cannot acccept players. ");
            }
        });
        entityManager.persist(soccerGame.get());
        return player;
    }

    @ReadOnly
    public List<SoccerGame> findAllSoccerGames() {
        String qlString = "SELECT g FROM SoccerGame as g";
        TypedQuery<SoccerGame> query = entityManager.createQuery(qlString, SoccerGame.class);
        return query.getResultList();
    }

    @ReadOnly
    public List<Player> findAllPlayers() {
        String qlString = "SELECT p FROM Player as p";
        TypedQuery<Player> query = entityManager.createQuery(qlString, Player.class);
        return query.getResultList();
    }
}
