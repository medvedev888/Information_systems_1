package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.dto.AddressDTO;
import me.vladislav.information_systems_1.exception.AddressNotFoundException;
import me.vladislav.information_systems_1.exception.LocationNotFoundException;
import me.vladislav.information_systems_1.mapper.AddressMapper;
import me.vladislav.information_systems_1.model.Address;
import me.vladislav.information_systems_1.model.Location;
import me.vladislav.information_systems_1.repository.AddressRepository;
import me.vladislav.information_systems_1.repository.LocationRepository;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AddressService {
    @Inject
    private AddressRepository addressRepository;

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private AddressMapper addressMapper;

    @Transactional
    public List<AddressDTO> getPage(Integer page, Integer size) {
        List<Address> addresses = addressRepository.getPage(page, size);
        return addresses.stream()
                .map(addressMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void save(AddressDTO addressDTO) {
        Address address = new Address();
        address.setStreet(addressDTO.getStreet());

        if (addressDTO.getTown() != null && addressDTO.getTown().getId() != null) {
            Location town = locationRepository.getById(addressDTO.getTown().getId())
                    .orElseThrow(() -> new LocationNotFoundException("Location not found"));
            address.setTown(town);
        }

        addressRepository.save(address);
    }

    @Transactional
    public void update(AddressDTO addressDTO) {
        Address address = addressRepository.getById(addressDTO.getId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));

        if (addressDTO.getStreet() != null) {
            address.setStreet(addressDTO.getStreet());
        }

        if (addressDTO.getTown() != null) {
            if (addressDTO.getTown().getId() == null) {
                throw new LocationNotFoundException("Location ID is required");
            }

            Location town = locationRepository.getById(addressDTO.getTown().getId())
                    .orElseThrow(() -> new LocationNotFoundException("Location not found"));

            address.setTown(town);
        }
    }

    @Transactional
    public void delete(Long id) {
        Address address = addressRepository.getById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        addressRepository.delete(address);
    }
}
