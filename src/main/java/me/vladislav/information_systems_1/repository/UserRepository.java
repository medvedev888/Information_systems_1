package me.vladislav.information_systems_1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import me.vladislav.information_systems_1.model.User;

import java.util.Optional;

@ApplicationScoped
public class UserRepository {
    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    public Optional<User> getByLogin(String login) {
        try {
            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public void save(User user) {
        entityManager.persist(user);
    }

}
