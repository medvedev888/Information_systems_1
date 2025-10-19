package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.dto.CoordinatesDTO;
import me.vladislav.information_systems_1.dto.PageResponse;
import me.vladislav.information_systems_1.exception.CoordinatesNotFoundException;
import me.vladislav.information_systems_1.exception.RelatedEntityDeletionException;
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
    public PageResponse<CoordinatesDTO> getPage(Integer page, Integer size) {
        List<CoordinatesDTO> coordinates = coordinatesRepository.getPage(page, size).stream()
                .map(coordinatesMapper::toDTO)
                .toList();
        long totalCount = coordinatesRepository.count();
        Integer totalPages = (int) Math.ceil((double) totalCount / size);
        return new PageResponse<>(coordinates, totalPages);
    }

    @Transactional
    public CoordinatesDTO save(CoordinatesDTO coordinatesDTO) {
        Coordinates coordinates = new Coordinates();

        coordinates.setX(coordinatesDTO.getX());
        coordinates.setY(coordinatesDTO.getY());

        coordinatesRepository.save(coordinates);

        return coordinatesMapper.toDTO(coordinates);
    }

    @Transactional
    public CoordinatesDTO update(CoordinatesDTO coordinatesDTO) {
        Coordinates coordinates = coordinatesRepository.getById(coordinatesDTO.getId())
                .orElseThrow(() -> new CoordinatesNotFoundException("Coordinates not found"));

        if (coordinatesDTO.getX() != null) {
            coordinates.setX(coordinatesDTO.getX());
        }

        if (coordinatesDTO.getY() != null) {
            coordinates.setY(coordinatesDTO.getY());
        }
        return coordinatesMapper.toDTO(coordinates);
    }

    @Transactional
    public CoordinatesDTO delete(Long id) {
        Coordinates coordinates = coordinatesRepository.getById(id)
                .orElseThrow(() -> new CoordinatesNotFoundException("Coordinates not found"));

        if (coordinatesRepository.getFreeCoordinates().stream().noneMatch(c -> c.getId().equals(id))) {
            throw new RelatedEntityDeletionException("Coordinates is used by an organization");
        }
        coordinatesRepository.delete(coordinates);

        CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
        coordinatesDTO.setId(id);
        return coordinatesDTO;
    }
}
