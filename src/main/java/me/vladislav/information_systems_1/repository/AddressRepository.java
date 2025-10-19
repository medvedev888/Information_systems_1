package me.vladislav.information_systems_1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.vladislav.information_systems_1.model.Address;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@ApplicationScoped
public class AddressRepository {
    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    private static final Map<String, String> ALLOWED_FIELDS = Map.of(
            "street", "street"
    );

    public Optional<Address> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Address.class, id));
    }

    public List<Address> getFreeAddresses() {
        return entityManager.createQuery(
                "SELECT a FROM Address a " +
                        "WHERE a.id NOT IN (SELECT o.officialAddress.id FROM Organization o WHERE o.officialAddress IS NOT NULL) " +
                        "AND a.id NOT IN (SELECT o.postalAddress.id FROM Organization o WHERE o.postalAddress IS NOT NULL)",
                Address.class
        ).getResultList();
    }

    public List<Address> getPage(int page, int size,
                                 String filterField, String filterValue,
                                 String sortField, String sortOrder) {

        StringBuilder jpql = new StringBuilder("SELECT a FROM Address a");

        boolean hasFilter = filterField != null && ALLOWED_FIELDS.containsKey(filterField)
                && filterValue != null && !filterValue.isBlank();

        if (hasFilter) {
            jpql.append(" WHERE LOWER(a.")
                    .append(ALLOWED_FIELDS.get(filterField))
                    .append(") LIKE :value");
        }

        if (sortField != null && ALLOWED_FIELDS.containsKey(sortField)) {
            String order = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
            jpql.append(" ORDER BY a.")
                    .append(ALLOWED_FIELDS.get(sortField))
                    .append(" ")
                    .append(order);
        }

        var query = entityManager.createQuery(jpql.toString(), Address.class);

        if (hasFilter) {
            query.setParameter("value", "%" + filterValue.toLowerCase() + "%");
        }

        return query
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countWithFilter(String filterField, String filterValue) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(a) FROM Address a");
        boolean hasFilter = filterField != null && ALLOWED_FIELDS.containsKey(filterField)
                && filterValue != null && !filterValue.isBlank();

        if (hasFilter) {
            jpql.append(" WHERE LOWER(a.")
                    .append(ALLOWED_FIELDS.get(filterField))
                    .append(") LIKE :value");
        }

        var query = entityManager.createQuery(jpql.toString(), Long.class);
        if (hasFilter) {
            query.setParameter("value", "%" + filterValue.toLowerCase() + "%");
        }

        return query.getSingleResult();
    }

    public void save(Address address) {
        entityManager.persist(address);
    }

    public void delete(Address address) {
        entityManager.remove(address);
    }
}
