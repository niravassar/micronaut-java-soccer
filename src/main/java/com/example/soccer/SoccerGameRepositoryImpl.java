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
    public Optional<SoccerGame> findById(long id) {
        return Optional.ofNullable(entityManager.find(SoccerGame.class, id));
    }

    @Override
    @Transactional
    public SoccerGame save(@NotBlank String name, int minPlayers, int maxPlayers) {
        SoccerGame soccerGame = new SoccerGame(name, minPlayers, maxPlayers);
        entityManager.persist(soccerGame);
        return soccerGame;
    }

    @Override
    @Transactional
    public Player savePlayer(@NotBlank String name, int age) {
        Player player = new Player(name, age);
        entityManager.persist(player);
        return player;
    }

    @ReadOnly
    public List<SoccerGame> findAll() {
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
