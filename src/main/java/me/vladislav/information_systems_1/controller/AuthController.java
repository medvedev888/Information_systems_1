package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.AuthRequest;
import me.vladislav.information_systems_1.dto.AuthResponse;
import me.vladislav.information_systems_1.dto.UserDTO;
import me.vladislav.information_systems_1.exception.InvalidCredentialsException;
import me.vladislav.information_systems_1.model.Role;
import me.vladislav.information_systems_1.service.UserService;
import me.vladislav.information_systems_1.utils.JwtUtil;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AuthController {
    @Inject
    private UserService userService;

    @POST
    @Path("/register")
    public Response register(@Valid AuthRequest request) {
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin(request.getLogin());
        userDTO.setPassword(request.getPassword());
        userDTO.setRole(Role.USER);

        UserDTO savedUserDTO = userService.registerNewUserAccount(userDTO);

        String token = JwtUtil.generateToken(savedUserDTO.getLogin(), savedUserDTO.getRole().name());
        AuthResponse response = new AuthResponse(token, new UserDTO(savedUserDTO.getId(), savedUserDTO.getLogin(), savedUserDTO.getPassword(), savedUserDTO.getRole()));
        return Response.status(Response.Status.CREATED)
                .header("Authorization", "Bearer " + token)
                .entity(response)
                .build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid AuthRequest request) {
        UserDTO userDTO = userService.getUserByLogin(request.getLogin());

        if (!userService.confirmUserPassword(request.getPassword(), userDTO.getPassword())) {
            throw new InvalidCredentialsException("Invalid login or password");
        }

        String token = JwtUtil.generateToken(userDTO.getLogin(), userDTO.getRole().name());
        AuthResponse response = new AuthResponse(token, new UserDTO(userDTO.getId(), userDTO.getLogin(), null, userDTO.getRole()));
        return Response.ok()
                .header("Authorization", "Bearer " + token)
                .entity(response)
                .build();
    }
}
