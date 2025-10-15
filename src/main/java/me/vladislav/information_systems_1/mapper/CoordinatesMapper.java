package me.vladislav.information_systems_1.mapper;

import me.vladislav.information_systems_1.dto.CoordinatesDTO;
import me.vladislav.information_systems_1.model.Coordinates;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface CoordinatesMapper {
    CoordinatesDTO toDTO(Coordinates coordinates);

    Coordinates toEntity(CoordinatesDTO dto);
}
