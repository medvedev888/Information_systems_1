package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.OrganizationDTO;
import me.vladislav.information_systems_1.exception.AddressNotFoundException;
import me.vladislav.information_systems_1.exception.CoordinatesNotFoundException;
import me.vladislav.information_systems_1.exception.OrganizationNotFoundException;
import me.vladislav.information_systems_1.service.OrganizationService;

import java.util.List;
import java.util.Map;

@Path("/organizations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class OrganizationController {
    @Inject
    private OrganizationService organizationService;

    @GET
    public Response getPage(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        List<OrganizationDTO> organizations = organizationService.getPage(page, size);
        return Response.ok(organizations).build();
    }

    @POST
    public Response create(@Valid OrganizationDTO organizationDTO) {
        try {
            organizationService.save(organizationDTO);
            return Response.status(Response.Status.CREATED).build();
        } catch (CoordinatesNotFoundException | AddressNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("success", false, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Could not create organization"))
                    .build();
        }
    }

    @PATCH
    public Response update(@Valid OrganizationDTO organizationDTO) {
        try {
            organizationService.update(organizationDTO);
            return Response.noContent().build();
        } catch (OrganizationNotFoundException | CoordinatesNotFoundException | AddressNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("success", false, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Could not update organization"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        try {
            organizationService.delete(id);
            return Response.noContent().build();
        } catch (OrganizationNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("success", false, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Could not delete organization"))
                    .build();
        }
    }
}
