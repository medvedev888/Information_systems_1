package me.vladislav.information_systems_1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.vladislav.information_systems_1.model.OrganizationType;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDTO {
    private Integer id;

    @NotBlank(message = "Name is required and cannot be blank")
    private String name;

    @NotNull(message = "Coordinates is required")
    private CoordinatesDTO coordinates;

    private java.util.Date creationDate;

    @NotNull(message = "Official address is required")
    private AddressDTO officialAddress;

    @Positive(message = "Annual turnover must be greater than 0")
    private Double annualTurnover;

    @Positive(message = "Employees count must be greater than 0")
    @NotNull(message = "Employees count is required")
    private Integer employeesCount;

    @Positive(message = "Rating must be greater than 0")
    @NotNull(message = "Rating is required")
    private Double rating;

    private String fullName;

    @NotNull(message = "Type is required")
    private OrganizationType type;

    private AddressDTO postalAddress;
}
