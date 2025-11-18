package me.vladislav.information_systems_1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import me.vladislav.information_systems_1.dto.*;
import me.vladislav.information_systems_1.exception.*;
import me.vladislav.information_systems_1.mapper.EntityMapper;
import me.vladislav.information_systems_1.model.*;
import me.vladislav.information_systems_1.repository.AddressRepository;
import me.vladislav.information_systems_1.repository.CoordinatesRepository;
import me.vladislav.information_systems_1.repository.OrganizationRepository;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class OrganizationService {

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private CoordinatesRepository coordinatesRepository;

    @Inject
    private AddressRepository addressRepository;

    @Inject
    private ImportHistoryService importHistoryService;

    @Inject
    private EntityMapper entityMapper;

    @PersistenceContext(unitName = "Lab1PU")
    private EntityManager entityManager;

    @Transactional
    public PageResponse<OrganizationDTO> getPage(Integer page,
                                                 Integer size,
                                                 String filterField,
                                                 String filterValue,
                                                 String sortField,
                                                 String sortOrder) {
        List<OrganizationDTO> organizations = organizationRepository.getPage(page, size, filterField, filterValue, sortField, sortOrder)
                .stream()
                .map(entityMapper::toDTO)
                .toList();
        long totalCount = organizationRepository.countWithFilter(filterField, filterValue);
        Integer totalPages = (int) Math.ceil((double) totalCount / size);

        return new PageResponse<>(organizations, totalPages);
    }


    @Transactional
    public List<OrganizationDTO> importFromJson(InputStream file, String login) {
        ImportHistoryDTO historyDTO = new ImportHistoryDTO();
        historyDTO.setLogin(login);
        int importedCount = 0;

        List<OrganizationDTO> dtos;
        try {
            String text = new String(file.readAllBytes(), StandardCharsets.UTF_8).replace("\uFEFF", "");
            int startIdx = Math.min(
                    text.indexOf('[') != -1 ? text.indexOf('[') : text.indexOf('{'),
                    text.indexOf('{') != -1 ? text.indexOf('{') : text.indexOf('[')
            );
            int endIdx = Math.max(text.lastIndexOf(']'), text.lastIndexOf('}'));
            text = text.substring(startIdx, endIdx + 1).trim();

            ObjectMapper mapper = new ObjectMapper();
            dtos = mapper.readValue(text, mapper.getTypeFactory().constructCollectionType(List.class, OrganizationDTO.class));
        } catch (Exception e) {
            historyDTO.setStatus(Status.FAILED);
            historyDTO.setImportedCount(0);
            importHistoryService.add(historyDTO);
            throw new ImportParseException("Failed to read or parse file", e);
        }

        try {
            Set<Long> usedCoordinatesIds = new HashSet<>();
            Set<Long> usedOfficialAddressIds = new HashSet<>();
            Set<Long> usedPostalAddressIds = new HashSet<>();

            List<Organization> entities = dtos.stream()
                    .map(dto -> {
                        // Блокируем координаты и адрес для записи
                        Coordinates coord = entityManager.find(
                                Coordinates.class, dto.getCoordinates().getId(), LockModeType.PESSIMISTIC_WRITE);
                        Address official = entityManager.find(
                                Address.class, dto.getOfficialAddress().getId(), LockModeType.PESSIMISTIC_WRITE);

                        if (organizationRepository.existsByCoordinates(coord)) {
                            throw new CoordinatesAlreadyUsedException("Coordinates заняты");
                        }
                        if (organizationRepository.existsByAddress(official)) {
                            throw new AddressAlreadyUsedException("Official address занят");
                        }

                        // Проверка на дубли внутри файла
                        if (!usedCoordinatesIds.add(coord.getId())) {
                            throw new CoordinatesAlreadyUsedException("Coordinates duplicated in import");
                        }
                        if (!usedOfficialAddressIds.add(official.getId())) {
                            throw new AddressAlreadyUsedException("Official address duplicated in import");
                        }
                        if (dto.getPostalAddress() != null && dto.getPostalAddress().getId() != null) {
                            Long postalId = dto.getPostalAddress().getId();
                            if (!usedPostalAddressIds.add(postalId)) {
                                throw new AddressAlreadyUsedException("Postal address duplicated in import");
                            }
                        }

                        return mapDtoToEntity(dto);
                    })
                    .toList();

            entities.forEach(organizationRepository::save);
            importedCount = entities.size();

            historyDTO.setStatus(Status.SUCCESS);
            historyDTO.setImportedCount(importedCount);
            importHistoryService.add(historyDTO);

            return entities.stream().map(entityMapper::toDTO).toList();
        } catch (Exception e) {
            historyDTO.setStatus(Status.FAILED);
            historyDTO.setImportedCount(0);
            importHistoryService.add(historyDTO);
            throw new ImportParseException("Import error: " + e.getMessage(), e);
        }
    }


    @Transactional
    public List<AddressDTO> getFreeOfficialAddresses() {
        return addressRepository.getFreeAddresses()
                .stream()
                .map(entityMapper::toDTO)
                .toList();
    }


    @Transactional
    public List<AddressDTO> getFreePostalAddresses() {
        List<AddressDTO> freePostalAddresses = new java.util.ArrayList<>(addressRepository.getFreeAddresses()
                .stream()
                .map(entityMapper::toDTO)
                .toList());

        freePostalAddresses.add(null);
        return freePostalAddresses;
    }


    @Transactional
    public List<CoordinatesDTO> getFreeCoordinates() {
        return coordinatesRepository.getFreeCoordinates()
                .stream()
                .map(entityMapper::toDTO)
                .toList();
    }


    @Transactional
    public OrganizationDTO save(OrganizationDTO organizationDTO) {
        Organization organization = mapDtoToEntity(organizationDTO);
        organizationRepository.save(organization);
        return entityMapper.toDTO(organization);
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

        if (organizationDTO.getPostalAddress() == null) {
            organization.setPostalAddress(null);
        } else {
            if (organizationDTO.getPostalAddress().getId() == null) {
                throw new AddressNotFoundException("Postal address ID is required");
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

        return entityMapper.toDTO(organization);
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


    // 1) Количество организаций с rating < заданного
    public long countByRating(Double rating) {
        return organizationRepository.countByRatingLessThan(rating);
    }

    // 2) Получить массив организаций по type
    public List<OrganizationDTO> getByType(OrganizationType type) {
        return organizationRepository.findByType(type.name())
                .stream()
                .map(entityMapper::toDTO)
                .toList();
    }

    // 3) Уникальные fullName
    public List<String> getUniqueFullNames() {
        return organizationRepository.findDistinctFullNames();
    }

    // 4) Объединить организации
    @Transactional
    public OrganizationDTO mergeCreateNew(OrganizationDTO org1Dto, OrganizationDTO org2Dto, String name, AddressDTO officialAddress) {
        Integer organizationID = organizationRepository.mergeOrganizations(org1Dto.getId(), org2Dto.getId(), name, officialAddress.getId());
        return entityMapper
                .toDTO(organizationRepository.getById(organizationID)
                        .orElseThrow(() -> new OrganizationNotFoundException("Organization not found after merge, id=" + organizationID)));
    }

    // 5) Поглощение одной организацией другой
    @Transactional
    public void absorbOrganization(OrganizationDTO acquirerDto, OrganizationDTO victimDto) {
        organizationRepository.absorbOrganization(acquirerDto.getId(), victimDto.getId());
    }


    private Organization mapDtoToEntity(OrganizationDTO dto) {
        Organization organization = new Organization();

        organization.setName(dto.getName());

        // Проверка координат
        Coordinates coordinates = coordinatesRepository.getById(dto.getCoordinates().getId())
                .orElseThrow(() -> new CoordinatesNotFoundException("Coordinates not found"));
        boolean coordinatesTaken = organizationRepository.existsByCoordinates(coordinates);
        if (coordinatesTaken) {
            throw new CoordinatesAlreadyUsedException("Coordinates with id " + coordinates.getId() + " already assigned");
        }
        organization.setCoordinates(coordinates);

        // Проверка официального адреса
        Address officialAddress = addressRepository.getById(dto.getOfficialAddress().getId())
                .orElseThrow(() -> new AddressNotFoundException("Official address not found"));
        boolean officialTaken = organizationRepository.existsByAddress(officialAddress);
        if (officialTaken) {
            throw new AddressAlreadyUsedException("Official address with id " + officialAddress.getId() + " already assigned");
        }
        organization.setOfficialAddress(officialAddress);

        // Проверка почтового адреса (если есть)
        if (dto.getPostalAddress() != null && dto.getPostalAddress().getId() != null) {
            Address postalAddress = addressRepository.getById(dto.getPostalAddress().getId())
                    .orElseThrow(() -> new AddressNotFoundException("Postal address not found"));
            boolean postalTaken = organizationRepository.existsByAddress(postalAddress);
            if (postalTaken) {
                throw new AddressAlreadyUsedException("Postal address with id " + postalAddress.getId() + " already assigned");
            }
            organization.setPostalAddress(postalAddress);
        }

        organization.setAnnualTurnover(dto.getAnnualTurnover());
        organization.setEmployeesCount(dto.getEmployeesCount());
        organization.setRating(dto.getRating());
        organization.setFullName(dto.getFullName());
        organization.setType(dto.getType());

        return organization;
    }

}
