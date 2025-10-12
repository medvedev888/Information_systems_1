package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.dto.LocationDTO;
import me.vladislav.information_systems_1.exception.LocationNotFoundException;
import me.vladislav.information_systems_1.mapper.LocationMapper;
import me.vladislav.information_systems_1.model.Location;
import me.vladislav.information_systems_1.repository.LocationRepository;

import java.util.List;

@ApplicationScoped
public class LocationService {

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private LocationMapper locationMapper;

    @Transactional
    public List<LocationDTO> getPage(Integer page, Integer size) {
        List<Location> locations = locationRepository.getPage(page, size);
        return locations.stream()
                .map(locationMapper::toDTO)
                .toList();
    }

    @Transactional
    public LocationDTO save(LocationDTO locationDTO) {
        Location location = new Location();

        location.setX(locationDTO.getX());
        location.setY(locationDTO.getY());
        location.setZ(locationDTO.getZ());

        locationRepository.save(location);
        return locationMapper.toDTO(location);
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
        return locationMapper.toDTO(location);
    }

    @Transactional
    public LocationDTO delete(Long id) {
        Location location = locationRepository.getById(id)
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));
        locationRepository.delete(location);

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setId(id);
        return locationDTO;
    }
}
