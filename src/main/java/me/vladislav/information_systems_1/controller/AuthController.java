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
import me.vladislav.information_systems_1.exception.UserAlreadyExistException;
import me.vladislav.information_systems_1.exception.UserNotFoundException;
import me.vladislav.information_systems_1.service.UserService;
import me.vladislav.information_systems_1.utils.JwtUtil;

import java.util.Map;

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
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setLogin(request.getLogin());
            userDTO.setPassword(request.getPassword());

            UserDTO savedUserDTO = userService.registerNewUserAccount(userDTO);

            String token = JwtUtil.generateToken(savedUserDTO.getLogin());
            AuthResponse response = new AuthResponse(token, new UserDTO(savedUserDTO.getId(), savedUserDTO.getLogin(), savedUserDTO.getPassword()));
            return Response.status(Response.Status.CREATED)
                    .header("Authorization", "Bearer " + token)
                    .entity(response)
                    .build();
        } catch (UserAlreadyExistException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("success", false, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Unexpected error"))
                    .build();
        }
    }

    @POST
    @Path("/login")
    public Response login(@Valid AuthRequest request) {
        try {
            UserDTO userDTO = userService.getUserByLogin(request.getLogin());
            if (userService.confirmUserPassword(request.getPassword(), userDTO.getPassword())) {
                String token = JwtUtil.generateToken(userDTO.getLogin());
                AuthResponse response = new AuthResponse(token, new UserDTO(userDTO.getId(), userDTO.getLogin(), null));
                return Response.ok()
                        .header("Authorization", "Bearer " + token)
                        .entity(response)
                        .build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of("success", false, "message", "Invalid login or password"))
                        .build();
            }
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("success", false, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Unexpected error"))
                    .build();
        }
    }
}
