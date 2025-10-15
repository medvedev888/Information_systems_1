package me.vladislav.information_systems_1.mapper;

import me.vladislav.information_systems_1.dto.LocationDTO;
import me.vladislav.information_systems_1.model.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface LocationMapper {
    LocationDTO toDTO(Location location);

    Location toEntity(LocationDTO dto);
}

