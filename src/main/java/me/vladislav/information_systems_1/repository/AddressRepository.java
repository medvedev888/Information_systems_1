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

    public Optional<Address> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Address.class, id));
    }

    public List<Address> getAll() {
        return entityManager.createQuery("FROM Address", Address.class).getResultList();
    }

    public void save(Address address) {
        entityManager.persist(address);
    }

    public void delete(Address address) {
        entityManager.remove(address);
    }
}
