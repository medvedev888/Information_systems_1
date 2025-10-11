package me.vladislav.information_systems_1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.vladislav.information_systems_1.model.Coordinates;

import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class CoordinatesRepository {
    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    public Optional<Coordinates> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Coordinates.class, id));
    }

    public List<Coordinates> getAll() {
        return entityManager.createQuery("FROM Coordinates", Coordinates.class).getResultList();
    }

    public void save(Coordinates coordinates) {
        entityManager.persist(coordinates);
    }

    public void delete(Coordinates coordinates) {
        entityManager.remove(coordinates);
    }
}
