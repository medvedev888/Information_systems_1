package me.vladislav.information_systems_1.cache;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

@ApplicationScoped
public class HibernateStatisticsProvider {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    public Statistics getStatistics() {
        return entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    }
}
