package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.OrganizationDTO;
import me.vladislav.information_systems_1.service.OrganizationService;

import java.util.List;

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
        organizationService.save(organizationDTO);
        return Response.status(Response.Status.CREATED).build();
    }

    @PATCH
    public Response update(@Valid OrganizationDTO organizationDTO) {
        organizationService.update(organizationDTO);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        organizationService.delete(id);
        return Response.noContent().build();

    }
}
