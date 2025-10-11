package me.vladislav.information_systems_1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    private Long x;

    private Float y;

    private Integer z;
}
