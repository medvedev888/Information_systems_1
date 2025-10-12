package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.CoordinatesDTO;
import me.vladislav.information_systems_1.service.CoordinatesService;
import me.vladislav.information_systems_1.service.EventService;

import java.util.List;

@Path("/coordinates")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CoordinatesController {
    @Inject
    private CoordinatesService coordinatesService;

    @Inject
    private EventService eventService;

    @GET
    public Response getPage(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        List<CoordinatesDTO> coordinatesList = coordinatesService.getPage(page, size);
        return Response.ok(coordinatesList).build();
    }

    @POST
    public Response create(@Valid CoordinatesDTO coordinatesDTO) {
        CoordinatesDTO coordinates = coordinatesService.save(coordinatesDTO);
        eventService.sendEvent("COORDINATES", "CREATED", coordinates);
        return Response.status(Response.Status.CREATED).build();
    }

    @PATCH
    public Response update(@Valid CoordinatesDTO coordinatesDTO) {
        CoordinatesDTO coordinates = coordinatesService.update(coordinatesDTO);
        eventService.sendEvent("COORDINATES", "UPDATED", coordinates);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        CoordinatesDTO coordinates = coordinatesService.delete(id);
        eventService.sendEvent("COORDINATES", "DELETED", coordinates);
        return Response.noContent().build();
    }
}
