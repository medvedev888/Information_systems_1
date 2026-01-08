package me.vladislav.information_systems_1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.vladislav.information_systems_1.model.ImportHistory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ImportHistoryRepository {
    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    private static final Map<String, String> ALLOWED_FIELDS = Map.of(
            "login", "user.login",
            "status", "status"
    );

    public List<ImportHistory> getPage(int page, int size,
                                       String filterField, String filterValue,
                                       String sortField, String sortOrder,
                                       String login) {

        StringBuilder jpql = new StringBuilder("SELECT ih FROM ImportHistory ih JOIN ih.user u WHERE 1=1 ");
        boolean hasFilter = filterField != null && ALLOWED_FIELDS.containsKey(filterField)
                && filterValue != null && !filterValue.isBlank();

        if (login != null && !login.isBlank()) {
            jpql.append(" AND LOWER(u.login) = :login ");
        }

        if (hasFilter) {
            String field = ALLOWED_FIELDS.get(filterField);
            if ("user.login".equals(field)) {
                jpql.append(" AND LOWER(u.login) LIKE :value ");
            } else {
                jpql.append(" AND LOWER(ih.").append(field).append(") LIKE :value ");
            }
        }

        if (sortField != null && ALLOWED_FIELDS.containsKey(sortField)) {
            String order = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
            jpql.append(" ORDER BY ").append(
                    "user".equals(sortField) ? "u.login " + order : "ih." + ALLOWED_FIELDS.get(sortField) + " " + order
            );
        }

        var query = entityManager.createQuery(jpql.toString(), ImportHistory.class);

        if (login != null && !login.isBlank()) {
            query.setParameter("login", login.toLowerCase());
        }
        if (hasFilter) {
            query.setParameter("value", "%" + filterValue.toLowerCase() + "%");
        }

        return query
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countWithFilter(String filterField, String filterValue, String login) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(ih) FROM ImportHistory ih JOIN ih.user u WHERE 1=1 ");
        boolean hasFilter = filterField != null && ALLOWED_FIELDS.containsKey(filterField)
                && filterValue != null && !filterValue.isBlank();

        if (login != null && !login.isBlank()) {
            jpql.append(" AND LOWER(u.login) = :login ");
        }

        if (hasFilter) {
            String field = ALLOWED_FIELDS.get(filterField);
            if ("user.login".equals(field)) {
                jpql.append(" AND LOWER(u.login) LIKE :value ");
            } else {
                jpql.append(" AND LOWER(ih.").append(field).append(") LIKE :value ");
            }
        }

        var query = entityManager.createQuery(jpql.toString(), Long.class);
        if (login != null && !login.isBlank()) {
            query.setParameter("login", login.toLowerCase());
        }
        if (hasFilter) {
            query.setParameter("value", "%" + filterValue.toLowerCase() + "%");
        }

        return query.getSingleResult();
    }

    public ImportHistory save(ImportHistory importHistory) {
        entityManager.persist(importHistory);
        return importHistory;
    }

    public Optional<ImportHistory> getById(Long id) {
        return Optional.ofNullable(entityManager.find(ImportHistory.class, id));
    }

}
