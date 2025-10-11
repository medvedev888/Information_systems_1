package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.dto.CoordinatesDTO;
import me.vladislav.information_systems_1.exception.CoordinatesNotFoundException;
import me.vladislav.information_systems_1.mapper.CoordinatesMapper;
import me.vladislav.information_systems_1.model.Coordinates;
import me.vladislav.information_systems_1.repository.CoordinatesRepository;

import java.util.List;

@ApplicationScoped
public class CoordinatesService {
    @Inject
    private CoordinatesRepository coordinatesRepository;

    @Inject
    private CoordinatesMapper coordinatesMapper;

    @Transactional
    public List<CoordinatesDTO> getPage(Integer page, Integer size) {
        List<Coordinates> coordinatesList = coordinatesRepository.getPage(page, size);
        return coordinatesList.stream()
                .map(coordinatesMapper::toDTO)
                .toList();
    }

    @Transactional
    public void save(CoordinatesDTO coordinatesDTO) {
        Coordinates coordinates = new Coordinates();

        coordinates.setX(coordinatesDTO.getX());
        coordinates.setY(coordinatesDTO.getY());

        coordinatesRepository.save(coordinates);
    }

    @Transactional
    public void update(CoordinatesDTO coordinatesDTO) {
        Coordinates coordinates = coordinatesRepository.getById(coordinatesDTO.getId())
                .orElseThrow(() -> new CoordinatesNotFoundException("Coordinates not found"));

        if (coordinatesDTO.getX() != null) {
            coordinates.setX(coordinatesDTO.getX());
        }

        if (coordinatesDTO.getY() != null) {
            coordinates.setY(coordinatesDTO.getY());
        }
    }

    @Transactional
    public void delete(Long id) {
        Coordinates coordinates = coordinatesRepository.getById(id)
                .orElseThrow(() -> new CoordinatesNotFoundException("Coordinates not found"));
        coordinatesRepository.delete(coordinates);
    }
}
