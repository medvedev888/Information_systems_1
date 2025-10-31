package me.vladislav.information_systems_1.mapper;

import me.vladislav.information_systems_1.dto.AddressDTO;
import me.vladislav.information_systems_1.dto.CoordinatesDTO;
import me.vladislav.information_systems_1.dto.LocationDTO;
import me.vladislav.information_systems_1.dto.OrganizationDTO;
import me.vladislav.information_systems_1.model.Address;
import me.vladislav.information_systems_1.model.Coordinates;
import me.vladislav.information_systems_1.model.Location;
import me.vladislav.information_systems_1.model.Organization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface EntityMapper {

    AddressDTO toDTO(Address address);
    Address toEntity(AddressDTO dto);

    CoordinatesDTO toDTO(Coordinates coordinates);
    Coordinates toEntity(CoordinatesDTO dto);

    LocationDTO toDTO(Location location);
    Location toEntity(LocationDTO dto);

    OrganizationDTO toDTO(Organization organization);
    Organization toEntity(OrganizationDTO dto);
}

