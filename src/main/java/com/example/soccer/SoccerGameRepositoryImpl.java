package com.example.soccer;

import com.example.soccer.domain.SoccerGame;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton // <1>
public class SoccerGameRepositoryImpl implements SoccerGameRepository {

    private static final List<String> VALID_PROPERTY_NAMES = Arrays.asList("id", "name");

    private final EntityManager entityManager;  // <2>
    private final ApplicationConfiguration applicationConfiguration;

    public SoccerGameRepositoryImpl(EntityManager entityManager, // <2>
                                    ApplicationConfiguration applicationConfiguration) {
        this.entityManager = entityManager;
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    @ReadOnly // <3>
    public Optional<SoccerGame> findById(long id) {
        return Optional.ofNullable(entityManager.find(SoccerGame.class, id));
    }

    @Override
    @Transactional // <4>
    public SoccerGame save(@NotBlank String name) {
        SoccerGame soccerGame = new SoccerGame(name);
        entityManager.persist(soccerGame);
        return soccerGame;
    }

    @ReadOnly // <3>
    public List<SoccerGame> findAll(@NotNull SortingAndOrderArguments args) {
        String qlString = "SELECT g FROM SoccerGame as g";
        if (args.getOrder().isPresent() && args.getSort().isPresent() && VALID_PROPERTY_NAMES.contains(args.getSort().get())) {
            qlString += " ORDER BY g." + args.getSort().get() + ' ' + args.getOrder().get().toLowerCase();
        }
        TypedQuery<SoccerGame> query = entityManager.createQuery(qlString, SoccerGame.class);
        query.setMaxResults(args.getMax().orElseGet(applicationConfiguration::getMax));
        args.getOffset().ifPresent(query::setFirstResult);

        return query.getResultList();
    }
}
