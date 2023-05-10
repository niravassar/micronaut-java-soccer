package com.example.soccer;

import com.example.soccer.domain.Soccer;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton // <1>
public class GenreRepositoryImpl implements GenreRepository {

    private static final List<String> VALID_PROPERTY_NAMES = Arrays.asList("id", "name");

    private final EntityManager entityManager;  // <2>
    private final ApplicationConfiguration applicationConfiguration;

    public GenreRepositoryImpl(EntityManager entityManager, // <2>
                               ApplicationConfiguration applicationConfiguration) {
        this.entityManager = entityManager;
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    @ReadOnly // <3>
    public Optional<Soccer> findById(long id) {
        return Optional.ofNullable(entityManager.find(Soccer.class, id));
    }

    @Override
    @Transactional // <4>
    public Soccer save(@NotBlank String name) {
        Soccer soccer = new Soccer(name);
        entityManager.persist(soccer);
        return soccer;
    }

    @Override
    @Transactional // <4>
    public void deleteById(long id) {
        findById(id).ifPresent(entityManager::remove);
    }

    @ReadOnly // <3>
    public List<Soccer> findAll(@NotNull SortingAndOrderArguments args) {
        String qlString = "SELECT g FROM Soccer as g";
        if (args.getOrder().isPresent() && args.getSort().isPresent() && VALID_PROPERTY_NAMES.contains(args.getSort().get())) {
            qlString += " ORDER BY g." + args.getSort().get() + ' ' + args.getOrder().get().toLowerCase();
        }
        TypedQuery<Soccer> query = entityManager.createQuery(qlString, Soccer.class);
        query.setMaxResults(args.getMax().orElseGet(applicationConfiguration::getMax));
        args.getOffset().ifPresent(query::setFirstResult);

        return query.getResultList();
    }

    @Override
    @Transactional // <4>
    public int update(long id, @NotBlank String name) {
        return entityManager.createQuery("UPDATE Soccer g SET name = :name where id = :id")
                .setParameter("name", name)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    @Transactional // <4>
    public Soccer saveWithException(@NotBlank String name) {
        save(name);
        throw new PersistenceException();
    }
}
