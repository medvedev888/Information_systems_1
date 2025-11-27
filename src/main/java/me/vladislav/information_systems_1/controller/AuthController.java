package me.vladislav.information_systems_1.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import me.vladislav.information_systems_1.dto.*;
import me.vladislav.information_systems_1.exception.InvalidCredentialsException;
import me.vladislav.information_systems_1.exception.UserForbiddenException;
import me.vladislav.information_systems_1.model.Role;
import me.vladislav.information_systems_1.service.EventService;
import me.vladislav.information_systems_1.service.UserService;
import me.vladislav.information_systems_1.utils.JwtUtil;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AuthController {
    @Inject
    private UserService userService;

    @Context
    private SecurityContext securityContext;

    @Inject
    private EventService eventService;

    @POST
    @Path("/register")
    public Response register(@Valid AuthRequest request) {
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin(request.getLogin());
        userDTO.setPassword(request.getPassword());
        userDTO.setRole(Role.USER);

        UserDTO savedUserDTO = userService.registerNewUserAccount(userDTO);
        eventService.sendEvent("USER", "CREATED", savedUserDTO);

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
        UserDTO userDTO = userService.getUserByLogin(request.getLogin(), true);

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

    @PATCH
    @Path("/roles")
    public Response updateUserRole(RoleUpdateDTO dto) {

        if (!securityContext.isUserInRole("ADMIN")) {
            throw new UserForbiddenException("You do not have permission to perform this action.");
        }

        userService.updateRole(dto);
        return Response.ok().build();
    }


    @GET
    @Path("/users")
    @RolesAllowed("ADMIN")
    public Response getPage(@QueryParam("page") Integer pageParam,
                            @QueryParam("size") Integer sizeParam,
                            @QueryParam("filterField") String filterFieldParam,
                            @QueryParam("filterValue") String filterValueParam,
                            @QueryParam("sortField") String sortFieldParam,
                            @QueryParam("sortOrder") String sortOrderParam) {

        if (!securityContext.isUserInRole("ADMIN")) {
            throw new UserForbiddenException("You do not have permission to perform this action.");
        }

        int page = (pageParam == null || pageParam < 1) ? 1 : pageParam;
        int size = (sizeParam == null || sizeParam < 1) ? 10 : sizeParam;
        String filterField = (filterFieldParam == null) ? null : filterFieldParam.trim();
        String filterValue = (filterValueParam == null) ? null : filterValueParam.trim();
        String sortField = (sortFieldParam == null) ? null : sortFieldParam.trim();
        String sortOrder = (sortOrderParam == null) ? "asc" : sortOrderParam.trim().toLowerCase();

        PageResponse<UserDTO> response = userService.getPage(page, size, filterField, filterValue, sortField, sortOrder);
        return Response.ok(response).build();
    }

    @GET
    @Path("/me")
    public Response getCurrentUser() {
        String login = securityContext.getUserPrincipal().getName();
        UserDTO userDTO = userService.getUserByLogin(login, false);
        return Response.ok(userDTO).build();
    }

}
