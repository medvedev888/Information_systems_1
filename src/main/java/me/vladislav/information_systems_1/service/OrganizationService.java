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
    public OrganizationDTO save(OrganizationDTO organizationDTO) {
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

        return organizationMapper.toDTO(organization);
    }


    @Transactional
    public OrganizationDTO update(OrganizationDTO organizationDTO) {
        Organization organization = organizationRepository.getById(organizationDTO.getId())
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        if (organizationDTO.getName() != null) {
            organization.setName(organizationDTO.getName());
        }

        if (organizationDTO.getCoordinates() != null) {
            if (organizationDTO.getCoordinates().getId() == null) {
                throw new CoordinatesNotFoundException("Coordinates ID is required");
            }
            Coordinates coordinates = coordinatesRepository.getById(organizationDTO.getCoordinates().getId())
                    .orElseThrow(() -> new CoordinatesNotFoundException("Coordinates not found"));
            organization.setCoordinates(coordinates);
        }

        if (organizationDTO.getOfficialAddress() != null) {
            if (organizationDTO.getOfficialAddress().getId() == null) {
                throw new AddressNotFoundException("Official address ID is required");
            }
            Address officialAddress = addressRepository.getById(organizationDTO.getOfficialAddress().getId())
                    .orElseThrow(() -> new AddressNotFoundException("Official address not found"));
            organization.setOfficialAddress(officialAddress);
        }

        if (organizationDTO.getPostalAddress() != null) {
            if (organizationDTO.getPostalAddress().getId() == null) {
                organization.setPostalAddress(null);
            } else {
                Address postalAddress = addressRepository.getById(organizationDTO.getPostalAddress().getId())
                        .orElse(null);
                organization.setPostalAddress(postalAddress);
            }
        }


        if (organizationDTO.getAnnualTurnover() != null) {
            organization.setAnnualTurnover(organizationDTO.getAnnualTurnover());
        }

        if (organizationDTO.getEmployeesCount() != null) {
            organization.setEmployeesCount(organizationDTO.getEmployeesCount());
        }

        if (organizationDTO.getRating() != null) {
            organization.setRating(organizationDTO.getRating());
        }

        if (organizationDTO.getFullName() != null) {
            organization.setFullName(organizationDTO.getFullName());
        }

        if (organizationDTO.getType() != null) {
            organization.setType(organizationDTO.getType());
        }

        return organizationMapper.toDTO(organization);
    }

    @Transactional
    public OrganizationDTO delete(Integer id) {
        Organization organization = organizationRepository.getById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));
        organizationRepository.delete(organization);

        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId(id);
        return organizationDTO;
    }
}
