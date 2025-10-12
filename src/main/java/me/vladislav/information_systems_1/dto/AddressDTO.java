package me.vladislav.information_systems_1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long id;

    @NotNull(message = "Street is required")
    private String street;

    private LocationDTO town;
}
