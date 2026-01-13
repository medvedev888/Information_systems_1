package me.vladislav.information_systems_1.service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import me.vladislav.information_systems_1.dto.CoordinatesDTO;
import me.vladislav.information_systems_1.mapper.EntityMapper;
import me.vladislav.information_systems_1.model.Coordinates;
import me.vladislav.information_systems_1.repository.DBCPCoordinatesRepository;

@ApplicationScoped
public class DBCPCoordinatesService {

    @Inject
    private EntityMapper entityMapper;

    @Inject
    private DBCPCoordinatesRepository repository;

    public long count() {
        return repository.count();
    }

    public CoordinatesDTO save(CoordinatesDTO coordinatesDTO) {
        Coordinates coordinates = new Coordinates();

        coordinates.setX(coordinatesDTO.getX());
        coordinates.setY(coordinatesDTO.getY());

        repository.save(coordinates);

        return entityMapper.toDTO(coordinates);
    }
}

