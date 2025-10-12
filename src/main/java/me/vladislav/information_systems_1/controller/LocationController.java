package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.LocationDTO;
import me.vladislav.information_systems_1.service.EventService;
import me.vladislav.information_systems_1.service.LocationService;

import java.util.List;

@Path("/locations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class LocationController {
    @Inject
    private LocationService locationService;

    @Inject
    private EventService eventService;

    @GET
    public Response getPage(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        List<LocationDTO> locations = locationService.getPage(page, size);
        return Response.ok(locations).build();
    }

    @POST
    public Response create(@Valid LocationDTO locationDTO) {
        LocationDTO location = locationService.save(locationDTO);
        eventService.sendEvent("LOCATION", "CREATED", location);
        return Response.status(Response.Status.CREATED).build();
    }

    @PATCH
    public Response update(@Valid LocationDTO locationDTO) {
        LocationDTO location = locationService.update(locationDTO);
        eventService.sendEvent("LOCATION", "UPDATED", location);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        LocationDTO location = locationService.delete(id);
        eventService.sendEvent("LOCATION", "DELETED", location);
        return Response.noContent().build();
    }
}
