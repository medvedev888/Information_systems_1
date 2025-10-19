package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.AddressDTO;
import me.vladislav.information_systems_1.dto.CoordinatesDTO;
import me.vladislav.information_systems_1.dto.OrganizationDTO;
import me.vladislav.information_systems_1.dto.PageResponse;
import me.vladislav.information_systems_1.service.EventService;
import me.vladislav.information_systems_1.service.OrganizationService;

import java.util.List;

@Path("/organizations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class OrganizationController {
    @Inject
    private OrganizationService organizationService;

    @Inject
    private EventService eventService;

    @GET
    public Response getPage(@QueryParam("page") Integer pageParam,
                            @QueryParam("size") Integer sizeParam,
                            @QueryParam("filterField") String filterFieldParam,
                            @QueryParam("filterValue") String filterValueParam,
                            @QueryParam("sortField") String sortFieldParam,
                            @QueryParam("sortOrder") String sortOrderParam) {
        int page = (pageParam == null || pageParam < 1) ? 1 : pageParam;
        int size = (sizeParam == null || sizeParam < 1) ? 5 : sizeParam;
        String filterField = (filterFieldParam == null) ? null : filterFieldParam.trim();
        String filterValue = (filterValueParam == null) ? null : filterValueParam.trim();
        String sortField = (sortFieldParam == null) ? null : sortFieldParam.trim();
        String sortOrder = (sortOrderParam == null) ? "asc" : sortOrderParam.trim().toLowerCase();

        PageResponse<OrganizationDTO> response = organizationService.getPage(page, size, filterField, filterValue, sortField, sortOrder);
        return Response.ok(response).build();
    }

    @GET
    @Path("/free-addresses")
    public Response getFreeAddresses(@QueryParam("type") String type) {
        if (type.equalsIgnoreCase("official")) {
            List<AddressDTO> freeOfficialAddresses = organizationService.getFreeOfficialAddresses();
            return Response.ok(freeOfficialAddresses).build();
        } else if (type.equalsIgnoreCase("postal")) {
            List<AddressDTO> freePostalAddresses = organizationService.getFreePostalAddresses();
            return Response.ok(freePostalAddresses).build();
        }
        throw new IllegalArgumentException("Unknown type: " + type);
    }

    @GET
    @Path("/free-coordinates")
    public Response getFreeCoordinates() {
        List<CoordinatesDTO> freeCoordinates = organizationService.getFreeCoordinates();
        return Response.ok(freeCoordinates).build();
    }

    @POST
    public Response create(@Valid OrganizationDTO organizationDTO) {
        OrganizationDTO organization = organizationService.save(organizationDTO);
        eventService.sendEvent("ORGANIZATION", "CREATED", organization);
        return Response.status(Response.Status.CREATED).build();
    }

    @PATCH
    public Response update(@Valid OrganizationDTO organizationDTO) {
        OrganizationDTO organization = organizationService.update(organizationDTO);
        eventService.sendEvent("ORGANIZATION", "UPDATED", organization);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        OrganizationDTO organization = organizationService.delete(id);
        eventService.sendEvent("ORGANIZATION", "DELETED", organization);
        return Response.noContent().build();
    }
}
