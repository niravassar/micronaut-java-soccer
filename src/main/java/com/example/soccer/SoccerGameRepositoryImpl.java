package com.example.soccer;

import com.example.soccer.domain.SoccerGame;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
public class SoccerGameRepositoryImpl implements SoccerGameRepository {

    private static final List<String> VALID_PROPERTY_NAMES = Arrays.asList("id", "name");

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
    public SoccerGame save(@NotBlank String name) {
        SoccerGame soccerGame = new SoccerGame(name);
        entityManager.persist(soccerGame);
        return soccerGame;
    }

    @ReadOnly
    public List<SoccerGame> findAll() {
        String qlString = "SELECT g FROM SoccerGame as g";
        TypedQuery<SoccerGame> query = entityManager.createQuery(qlString, SoccerGame.class);
        return query.getResultList();
    }
}
