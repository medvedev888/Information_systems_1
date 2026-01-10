package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.cache.LogCacheStats;
import me.vladislav.information_systems_1.dto.AddressDTO;
import me.vladislav.information_systems_1.dto.LocationDTO;
import me.vladislav.information_systems_1.dto.PageResponse;
import me.vladislav.information_systems_1.exception.AddressNotFoundException;
import me.vladislav.information_systems_1.exception.LocationNotFoundException;
import me.vladislav.information_systems_1.exception.RelatedEntityDeletionException;
import me.vladislav.information_systems_1.mapper.EntityMapper;
import me.vladislav.information_systems_1.model.Address;
import me.vladislav.information_systems_1.model.Location;
import me.vladislav.information_systems_1.repository.AddressRepository;
import me.vladislav.information_systems_1.repository.LocationRepository;

import java.util.List;

@ApplicationScoped
@LogCacheStats
public class AddressService {
    @Inject
    private AddressRepository addressRepository;

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private EntityMapper entityMapper;

    @Transactional
    public PageResponse<AddressDTO> getPage(Integer page,
                                            Integer size,
                                            String filterField,
                                            String filterValue,
                                            String sortField,
                                            String sortOrder) {
        List<AddressDTO> addresses = addressRepository.getPage(page, size, filterField, filterValue, sortField, sortOrder)
                .stream()
                .map(entityMapper::toDTO)
                .toList();
        long totalCount = addressRepository.countWithFilter(filterField, filterValue);
        Integer totalPages = (int) Math.ceil((double) totalCount / size);
        return new PageResponse<>(addresses, totalPages);
    }

    @Transactional
    public List<LocationDTO> getFreeLocations() {
        List<LocationDTO> freeLocations = new java.util.ArrayList<>(locationRepository.getFreeLocations()
                .stream()
                .map(entityMapper::toDTO)
                .toList());

        freeLocations.add(null);
        return freeLocations;
    }

    @Transactional
    public AddressDTO save(AddressDTO addressDTO) {
        Address address = new Address();
        address.setStreet(addressDTO.getStreet());

        if (addressDTO.getTown() != null && addressDTO.getTown().getId() != null) {
            Location town = locationRepository.getById(addressDTO.getTown().getId())
                    .orElseThrow(() -> new LocationNotFoundException("Location not found"));
            address.setTown(town);
        }

        addressRepository.save(address);

        return entityMapper.toDTO(address);
    }

    @Transactional
    public AddressDTO update(AddressDTO addressDTO) {
        Address address = addressRepository.getById(addressDTO.getId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));

        if (addressDTO.getStreet() != null) {
            address.setStreet(addressDTO.getStreet());
        }

        if (addressDTO.getTown() == null) {
            address.setTown(null);
        } else {
            if (addressDTO.getTown().getId() == null) {
                throw new LocationNotFoundException("Location ID is required");
            } else {
                Location town = locationRepository.getById(addressDTO.getTown().getId())
                        .orElseThrow(null);
                address.setTown(town);
            }
        }

        return entityMapper.toDTO(address);
    }


    @Transactional
    public AddressDTO delete(Long id) {
        Address address = addressRepository.getById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));

        if (addressRepository.getFreeAddresses().stream().noneMatch(a -> a.getId().equals(id))) {
            throw new RelatedEntityDeletionException("Address is used by an organization");
        }
        addressRepository.delete(address);

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(id);
        return addressDTO;
    }
}
