package me.vladislav.information_systems_1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.vladislav.information_systems_1.model.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String login;
    private String password;
    private Role role;
}
