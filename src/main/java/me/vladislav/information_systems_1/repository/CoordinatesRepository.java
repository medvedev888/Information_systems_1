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

    public long count() {
        return entityManager
                .createQuery("SELECT COUNT(c) FROM Coordinates c", Long.class)
                .getSingleResult();
    }

    public Optional<Coordinates> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Coordinates.class, id));
    }

    public List<Coordinates> getAll() {
        return entityManager.createQuery("SELECT c FROM Coordinates c", Coordinates.class).getResultList();
    }

    public List<Coordinates> getFreeCoordinates() {
        return entityManager.createQuery(
                        "SELECT c FROM Coordinates c WHERE c NOT IN " +
                                "(SELECT o.coordinates FROM Organization o)", Coordinates.class)
                .getResultList();
    }

    public List<Coordinates> getPage(int page, int size) {
        return entityManager.createQuery("SELECT c FROM Coordinates c", Coordinates.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public void save(Coordinates coordinates) {
        entityManager.persist(coordinates);
    }

    public void delete(Coordinates coordinates) {
        entityManager.remove(coordinates);
    }
}
