package me.vladislav.information_systems_1.repository;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.vladislav.information_systems_1.model.Organization;

import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class OrganizationRepository {
    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    public Optional<Organization> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Organization.class, id));
    }

    public List<Organization> getAll() {
        return entityManager.createQuery("FROM Organization", Organization.class).getResultList();
    }

    public void save(Organization organization) {
        entityManager.persist(organization);
    }

    public void delete(Organization organization) {
        entityManager.remove(organization);
    }
}
