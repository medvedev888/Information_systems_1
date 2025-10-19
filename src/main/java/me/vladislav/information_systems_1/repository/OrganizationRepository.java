package me.vladislav.information_systems_1.repository;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.vladislav.information_systems_1.model.Location;
import me.vladislav.information_systems_1.model.Organization;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@ApplicationScoped
public class OrganizationRepository {
    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    private static final Map<String, String> ALLOWED_FIELDS = Map.of(
            "name", "name",
            "fullName", "fullName",
            "type", "type"
    );

    public Optional<Organization> getById(Integer id) {
        return Optional.ofNullable(entityManager.find(Organization.class, id));
    }

    public List<Organization> getPage(int page, int size,
                                      String filterField, String filterValue,
                                      String sortField, String sortOrder) {

        StringBuilder jpql = new StringBuilder("SELECT o FROM Organization o");
        boolean hasFilter = filterField != null && ALLOWED_FIELDS.containsKey(filterField)
                && filterValue != null && !filterValue.isBlank();

        if (hasFilter) {
            jpql.append(" WHERE LOWER(o.")
                    .append(ALLOWED_FIELDS.get(filterField))
                    .append(") LIKE :value");
        }

        if (sortField != null && ALLOWED_FIELDS.containsKey(sortField)) {
            String order = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
            jpql.append(" ORDER BY o.")
                    .append(ALLOWED_FIELDS.get(sortField))
                    .append(" ")
                    .append(order);
        }

        var query = entityManager.createQuery(jpql.toString(), Organization.class);
        if (hasFilter) {
            query.setParameter("value", "%" + filterValue.toLowerCase() + "%");
        }

        return query
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countWithFilter(String filterField, String filterValue) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(o) FROM Organization o");
        boolean hasFilter = filterField != null && ALLOWED_FIELDS.containsKey(filterField)
                && filterValue != null && !filterValue.isBlank();

        var query = entityManager.createQuery(jpql.toString(), Long.class);

        if (hasFilter) {
            jpql.append(" WHERE LOWER(o.")
                    .append(ALLOWED_FIELDS.get(filterField))
                    .append(") LIKE :value");
            query = entityManager.createQuery(jpql.toString(), Long.class);
            query.setParameter("value", "%" + filterValue.toLowerCase() + "%");
        }

        return query.getSingleResult();
    }

    public void save(Organization organization) {
        entityManager.persist(organization);
    }

    public void delete(Organization organization) {
        entityManager.remove(organization);
    }
}
