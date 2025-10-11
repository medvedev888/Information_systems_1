package me.vladislav.information_systems_1.mapper;

import me.vladislav.information_systems_1.dto.OrganizationDTO;
import me.vladislav.information_systems_1.model.Organization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {AddressMapper.class, CoordinatesMapper.class})
public interface OrganizationMapper {
    OrganizationDTO toDTO(Organization organization);

    Organization toEntity(OrganizationDTO dto);
}
