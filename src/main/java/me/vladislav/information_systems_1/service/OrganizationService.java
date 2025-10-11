package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.dto.OrganizationDTO;
import me.vladislav.information_systems_1.exception.AddressNotFoundException;
import me.vladislav.information_systems_1.exception.CoordinatesNotFoundException;
import me.vladislav.information_systems_1.exception.OrganizationNotFoundException;
import me.vladislav.information_systems_1.mapper.OrganizationMapper;
import me.vladislav.information_systems_1.model.Address;
import me.vladislav.information_systems_1.model.Coordinates;
import me.vladislav.information_systems_1.model.Organization;
import me.vladislav.information_systems_1.repository.AddressRepository;
import me.vladislav.information_systems_1.repository.CoordinatesRepository;
import me.vladislav.information_systems_1.repository.OrganizationRepository;

import java.util.List;

@ApplicationScoped
public class OrganizationService {

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private CoordinatesRepository coordinatesRepository;

    @Inject
    private AddressRepository addressRepository;

    @Inject
    private OrganizationMapper organizationMapper;

    @Transactional
    public List<OrganizationDTO> getPage(Integer page, Integer size) {
        List<Organization> organizations = organizationRepository.getPage(page, size);
        return organizations.stream()
                .map(organizationMapper::toDTO)
                .toList();
    }

    @Transactional
    public void save(OrganizationDTO organizationDTO) {
        Organization organization = new Organization();

        organization.setName(organizationDTO.getName());

        Coordinates coordinates = coordinatesRepository.getById(organizationDTO.getCoordinates().getId())
                .orElseThrow(() -> new CoordinatesNotFoundException("Coordinates not found"));
        organization.setCoordinates(coordinates);

        Address officialAddress = addressRepository.getById(organizationDTO.getOfficialAddress().getId())
                .orElseThrow(() -> new AddressNotFoundException("Official address not found"));
        organization.setOfficialAddress(officialAddress);

        if (organizationDTO.getPostalAddress() != null && organizationDTO.getPostalAddress().getId() != null) {
            Address postalAddress = addressRepository.getById(organizationDTO.getPostalAddress().getId())
                    .orElseThrow(() -> new AddressNotFoundException("Postal address not found"));
            organization.setPostalAddress(postalAddress);
        }

        organization.setAnnualTurnover(organizationDTO.getAnnualTurnover());

        organization.setEmployeesCount(organizationDTO.getEmployeesCount());

        organization.setRating(organizationDTO.getRating());

        organization.setFullName(organizationDTO.getFullName());

        organization.setType(organizationDTO.getType());

        organizationRepository.save(organization);
    }


    @Transactional
    public void update(OrganizationDTO organizationDTO) {
        Organization org = organizationRepository.getById(organizationDTO.getId())
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        if (organizationDTO.getName() != null) {
            org.setName(organizationDTO.getName());
        }

        if (organizationDTO.getCoordinates() != null) {
            if (organizationDTO.getCoordinates().getId() == null) {
                throw new CoordinatesNotFoundException("Coordinates ID is required");
            }
            Coordinates coordinates = coordinatesRepository.getById(organizationDTO.getCoordinates().getId())
                    .orElseThrow(() -> new CoordinatesNotFoundException("Coordinates not found"));
            org.setCoordinates(coordinates);
        }

        if (organizationDTO.getOfficialAddress() != null) {
            if (organizationDTO.getOfficialAddress().getId() == null) {
                throw new AddressNotFoundException("Official address ID is required");
            }
            Address officialAddress = addressRepository.getById(organizationDTO.getOfficialAddress().getId())
                    .orElseThrow(() -> new AddressNotFoundException("Official address not found"));
            org.setOfficialAddress(officialAddress);
        }

        if (organizationDTO.getPostalAddress() != null) {
            if (organizationDTO.getPostalAddress().getId() == null) {
                org.setPostalAddress(null);
            } else {
                Address postalAddress = addressRepository.getById(organizationDTO.getPostalAddress().getId())
                        .orElse(null);
                org.setPostalAddress(postalAddress);
            }
        }


        if (organizationDTO.getAnnualTurnover() != null) {
            org.setAnnualTurnover(organizationDTO.getAnnualTurnover());
        }

        if (organizationDTO.getEmployeesCount() != null) {
            org.setEmployeesCount(organizationDTO.getEmployeesCount());
        }

        if (organizationDTO.getRating() != null) {
            org.setRating(organizationDTO.getRating());
        }

        if (organizationDTO.getFullName() != null) {
            org.setFullName(organizationDTO.getFullName());
        }

        if (organizationDTO.getType() != null) {
            org.setType(organizationDTO.getType());
        }
    }

    @Transactional
    public void delete(Integer id) {
        Organization organization = organizationRepository.getById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));
        organizationRepository.delete(organization);
    }
}
