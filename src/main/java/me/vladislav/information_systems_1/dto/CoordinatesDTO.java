package me.vladislav.information_systems_1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoordinatesDTO {
    @NotNull(message = "X is required")
    private Float x;

    @NotNull(message = "X is required")
    @Min(value = -645, message = "Y must be greater than -646")
    private Integer y;
}
