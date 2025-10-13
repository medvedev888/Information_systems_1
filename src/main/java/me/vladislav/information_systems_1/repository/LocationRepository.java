package me.vladislav.information_systems_1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.vladislav.information_systems_1.model.Coordinates;
import me.vladislav.information_systems_1.model.Location;

import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class LocationRepository {
    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    public long count() {
        return entityManager
                .createQuery("SELECT COUNT(l) FROM Location l", Long.class)
                .getSingleResult();
    }

    public Optional<Location> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Location.class, id));
    }

    public List<Location> getAll() {
        return entityManager.createQuery("SELECT l FROM Location l", Location.class).getResultList();
    }

    public List<Location> getPage(int page, int size) {
        return entityManager.createQuery("SELECT l FROM Location l", Location.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public void save(Location location) {
        entityManager.persist(location);
    }

    public void delete(Location location) {
        entityManager.remove(location);
    }
}
