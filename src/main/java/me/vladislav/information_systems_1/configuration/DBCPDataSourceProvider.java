package me.vladislav.information_systems_1.configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import lombok.Getter;
import me.vladislav.information_systems_1.service.EventService;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

@ApplicationScoped
@Getter
public class DBCPDataSourceProvider {

    private BasicDataSource dataSource;

    @PostConstruct
    public void init() {
        dataSource = new BasicDataSource();

        String driver = System.getenv().get("DB_DRIVER");
        String url = System.getenv().get("DB_URL");
        String user = System.getenv().get("DB_USER");
        String password = System.getenv().get("DB_PASSWORD");

        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        // пул
        dataSource.setMinIdle(2);
        dataSource.setMaxIdle(5);
        dataSource.setMaxTotal(10);
        dataSource.setMaxWaitMillis(5000);

        // валидация
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(true);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @PreDestroy
    public void destroy() {
        try {
            if (dataSource != null) dataSource.close();
        } catch (Exception ignored) { }
    }
}
