package me.vladislav.information_systems_1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import me.vladislav.information_systems_1.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UserRepository {
    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    private static final Map<String, String> ALLOWED_FIELDS = Map.of(
            "login", "login",
            "role", "role"
    );


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


    public List<User> getPage(int page, int size,
                                      String filterField, String filterValue,
                                      String sortField, String sortOrder) {

        StringBuilder jpql = new StringBuilder("SELECT u FROM User u");
        boolean hasFilter = filterField != null && ALLOWED_FIELDS.containsKey(filterField)
                && filterValue != null && !filterValue.isBlank();

        if (hasFilter) {
            jpql.append(" WHERE LOWER(u.")
                    .append(ALLOWED_FIELDS.get(filterField))
                    .append(") LIKE :value");
        }

        if (sortField != null && ALLOWED_FIELDS.containsKey(sortField)) {
            String order = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
            jpql.append(" ORDER BY u.")
                    .append(ALLOWED_FIELDS.get(sortField))
                    .append(" ")
                    .append(order);
        }

        var query = entityManager.createQuery(jpql.toString(), User.class);
        if (hasFilter) {
            query.setParameter("value", "%" + filterValue.toLowerCase() + "%");
        }

        return query
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }


    public long countWithFilter(String filterField, String filterValue) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(u) FROM User u");
        boolean hasFilter = filterField != null && ALLOWED_FIELDS.containsKey(filterField)
                && filterValue != null && !filterValue.isBlank();

        if (hasFilter) {
            jpql.append(" WHERE LOWER(u.")
                    .append(ALLOWED_FIELDS.get(filterField))
                    .append(") LIKE :value");
        }

        var query = entityManager.createQuery(jpql.toString(), Long.class);
        if (hasFilter) {
            query.setParameter("value", "%" + filterValue.toLowerCase() + "%");
        }

        return query.getSingleResult();
    }

}
