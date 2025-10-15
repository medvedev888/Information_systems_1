package me.vladislav.information_systems_1.mapper;

import me.vladislav.information_systems_1.dto.AddressDTO;
import me.vladislav.information_systems_1.model.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = LocationMapper.class)
public interface AddressMapper {
    AddressDTO toDTO(Address address);

    Address toEntity(AddressDTO dto);
}
