package me.vladislav.information_systems_1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.vladislav.information_systems_1.model.Address;

import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class AddressRepository {
    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    public long count() {
        return entityManager
                .createQuery("SELECT COUNT(a) FROM Address a", Long.class)
                .getSingleResult();
    }

    public Optional<Address> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Address.class, id));
    }

    public List<Address> getAll() {
        return entityManager.createQuery("SELECT a FROM Address a", Address.class).getResultList();
    }

    public List<Address> getFreeAddresses() {
        return entityManager.createQuery(
                "SELECT a FROM Address a " +
                        "WHERE a.id NOT IN (SELECT o.officialAddress.id FROM Organization o WHERE o.officialAddress IS NOT NULL) " +
                        "AND a.id NOT IN (SELECT o.postalAddress.id FROM Organization o WHERE o.postalAddress IS NOT NULL)",
                Address.class
        ).getResultList();
    }

    public List<Address> getPage(Integer page, Integer size) {
        return entityManager.createQuery("SELECT a FROM Address a", Address.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public void save(Address address) {
        entityManager.persist(address);
    }

    public void delete(Address address) {
        entityManager.remove(address);
    }
}
