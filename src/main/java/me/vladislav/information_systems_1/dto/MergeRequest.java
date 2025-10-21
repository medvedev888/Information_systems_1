package me.vladislav.information_systems_1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MergeRequest {
    @NotNull(message = "First organization is required")
    public OrganizationDTO organization1;
    @NotNull(message = "Second organization is required")
    public OrganizationDTO organization2;
    @NotNull(message = "Name is required")
    public String name;
    @NotNull(message = "Official address is required")
    public AddressDTO officialAddress;
}
