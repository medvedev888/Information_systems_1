package me.vladislav.information_systems_1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import me.vladislav.information_systems_1.configuration.DBCPDataSourceProvider;
import me.vladislav.information_systems_1.model.Coordinates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@ApplicationScoped
public class DBCPCoordinatesRepository {

    @Inject
    DBCPDataSourceProvider dataSourceProvider;

    public long count() {
        String sql = "SELECT COUNT(*) FROM coordinates";

        try (Connection conn = dataSourceProvider.getDataSource().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            resultSet.next();
            return resultSet.getLong(1);

        } catch (Exception e) {
            throw new RuntimeException("Count failed", e);
        }
    }

    public void save(Coordinates coordinates) {
        String sql = "INSERT INTO coordinates (x, y) VALUES (?, ?)";

        try (Connection conn = dataSourceProvider.getDataSource().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, coordinates.getX());
                ps.setDouble(2, coordinates.getY());
                ps.executeUpdate();

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Save rolled back", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Transaction error", e);
        }
    }
}

