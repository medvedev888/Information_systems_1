package me.vladislav.information_systems_1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsorbRequest {
    @NotNull(message = "First organization is required")
    public OrganizationDTO acquirerOrganization;
    @NotNull(message = "Second organization is required")
    public OrganizationDTO victimOrganization;
}
