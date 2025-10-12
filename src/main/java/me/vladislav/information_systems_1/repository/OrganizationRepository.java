package me.vladislav.information_systems_1.repository;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.vladislav.information_systems_1.model.Location;
import me.vladislav.information_systems_1.model.Organization;

import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class OrganizationRepository {
    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    public Optional<Organization> getById(Integer id) {
        return Optional.ofNullable(entityManager.find(Organization.class, id));
    }

    public List<Organization> getAll() {
        return entityManager.createQuery("SELECT o FROM Organization o", Organization.class).getResultList();
    }

    public List<Organization> getPage(int page, int size) {
        return entityManager.createQuery("SELECT o FROM Organization o", Organization.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public void save(Organization organization) {
        entityManager.persist(organization);
    }

    public void delete(Organization organization) {
        entityManager.remove(organization);
    }
}
