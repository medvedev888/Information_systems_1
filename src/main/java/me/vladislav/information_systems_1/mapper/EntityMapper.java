package me.vladislav.information_systems_1.mapper;

import me.vladislav.information_systems_1.dto.*;
import me.vladislav.information_systems_1.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface EntityMapper {

    @Mapping(target = "password", ignore = true)
    UserDTO toDTO(User user);
    User toEntity(UserDTO dto);

    AddressDTO toDTO(Address address);
    Address toEntity(AddressDTO dto);

    CoordinatesDTO toDTO(Coordinates coordinates);
    Coordinates toEntity(CoordinatesDTO dto);

    LocationDTO toDTO(Location location);
    Location toEntity(LocationDTO dto);

    OrganizationDTO toDTO(Organization organization);
    Organization toEntity(OrganizationDTO dto);

    @Mapping(target = "login", source = "user.login")
    ImportHistoryDTO toDTO(ImportHistory importHistory);
}
