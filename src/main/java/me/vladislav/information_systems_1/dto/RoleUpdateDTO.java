package me.vladislav.information_systems_1.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdateDTO {
    @NotNull(message = "Id is required")
    private Long id;

    @NotNull(message = "Login is required")
    @NotEmpty(message = "Login cannot be empty")
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters long")
    private String login;

    @NotNull(message = "Role is required")
    private String role;
}
