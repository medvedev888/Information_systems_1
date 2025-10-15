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
public class AuthRequest {
    @NotNull(message = "Login is required")
    @NotEmpty(message = "Login cannot be empty")
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters long")
    public String login;
    @NotNull(message = "Password is required")
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters long")
    public String password;
}
