package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.cache.LogCacheStats;
import me.vladislav.information_systems_1.dto.LocationDTO;
import me.vladislav.information_systems_1.dto.PageResponse;
import me.vladislav.information_systems_1.exception.LocationNotFoundException;
import me.vladislav.information_systems_1.exception.RelatedEntityDeletionException;
import me.vladislav.information_systems_1.mapper.EntityMapper;
import me.vladislav.information_systems_1.model.Location;
import me.vladislav.information_systems_1.repository.LocationRepository;

import java.util.List;

@ApplicationScoped
@LogCacheStats
public class LocationService {

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private EntityMapper entityMapper;

    @Transactional
    public PageResponse<LocationDTO> getPage(Integer page, Integer size) {
        List<LocationDTO> locations = locationRepository.getPage(page, size).stream()
                .map(entityMapper::toDTO)
                .toList();
        long totalCount = locationRepository.count();
        Integer totalPages = (int) Math.ceil((double) totalCount / size);
        return new PageResponse<>(locations, totalPages);
    }

    @Transactional
    public LocationDTO save(LocationDTO locationDTO) {
        Location location = new Location();

        location.setX(locationDTO.getX());
        location.setY(locationDTO.getY());
        location.setZ(locationDTO.getZ());

        locationRepository.save(location);
        return entityMapper.toDTO(location);
    }

    @Transactional
    public LocationDTO update(LocationDTO locationDTO) {
        Location location = locationRepository.getById(locationDTO.getId())
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));

        if (locationDTO.getX() != null) {
            location.setX(locationDTO.getX());
        }

        if (locationDTO.getY() != null) {
            location.setY(locationDTO.getY());
        }

        if (locationDTO.getZ() != null) {
            location.setZ(locationDTO.getZ());
        }
        return entityMapper.toDTO(location);
    }

    @Transactional
    public LocationDTO delete(Long id) {
        Location location = locationRepository.getById(id)
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));

        if (locationRepository.getFreeLocations().stream().noneMatch(l -> l.getId().equals(id))) {
            throw new RelatedEntityDeletionException("Location is used by an address");
        }
        locationRepository.delete(location);

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setId(id);
        return locationDTO;
    }
}
