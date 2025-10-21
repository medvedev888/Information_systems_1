package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.AbsorbRequest;
import me.vladislav.information_systems_1.dto.MergeRequest;
import me.vladislav.information_systems_1.dto.OrganizationDTO;
import me.vladislav.information_systems_1.model.OrganizationType;
import me.vladislav.information_systems_1.service.EventService;
import me.vladislav.information_systems_1.service.OrganizationService;

import java.util.List;
import java.util.Map;

@Path("/organizations/operations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class OrganizationOperationsController {

    @Inject
    private OrganizationService organizationService;

    @Inject
    private EventService eventService;

    // 1) Получить количество организаций с rating < заданного
    @GET
    @Path("/count-by-rating")
    public Response countByRating(@QueryParam("rating") Double rating) {
        if (rating == null) {
            throw new IllegalArgumentException("rating is required");
        }
        long count = organizationService.countByRating(rating);
        return Response.ok(Map.of("count", count)).build();
    }

    // 2) Вернуть массив организаций по type
    @GET
    @Path("/by-type")
    public Response getByType(@QueryParam("type") String type) {
        OrganizationType organizationType;
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type is required");
        }
        try {
            organizationType = OrganizationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        List<OrganizationDTO> organizations = organizationService.getByType(organizationType);
        return Response.ok(organizations).build();
    }

    // 3) Вернуть массив уникальных fullName
    @GET
    @Path("/unique-fullnames")
    public Response getUniqueFullNames() {
        List<String> names = organizationService.getUniqueFullNames();
        return Response.ok(names).build();
    }

    // 4) Объединить организации и создать новую (перенести сотрудников)
    @POST
    @Path("/merge")
    public Response mergeOrganizations(MergeRequest request) {
        if (request.organization1.getId().equals(request.organization2.getId())) {
            throw new IllegalArgumentException("First organization and second organization must be different");
        }
        OrganizationDTO newOrganization = organizationService.mergeCreateNew(request.organization1,
                request.organization2,
                request.name,
                request.officialAddress);

        eventService.sendEvent("ORGANIZATION", "MERGED", newOrganization);
        return Response.status(Response.Status.CREATED).entity(newOrganization).build();
    }

    // 5) Поглощение (acquirer поглощает victim). name/address acquirer не меняются.
    @POST
    @Path("/absorb")
    public Response absorbOrganization(AbsorbRequest request) {
        if (request.acquirerOrganization.getId().equals(request.victimOrganization.getId())) {
            throw new IllegalArgumentException("First organization and second organization must be different");
        }
        organizationService.absorbOrganization(request.acquirerOrganization, request.victimOrganization);

        eventService.sendEvent("ORGANIZATION", "ABSORBED", Map.of("by", request.acquirerOrganization, "victim", request.victimOrganization));
        return Response.noContent().build();
    }
}
