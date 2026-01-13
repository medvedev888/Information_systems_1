package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.CoordinatesDTO;
import me.vladislav.information_systems_1.service.DBCPCoordinatesService;
import me.vladislav.information_systems_1.service.EventService;

import java.util.Map;

@Path("/dbcp/coordinates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class DBCPCoordinatesController {

    @Inject
    private DBCPCoordinatesService service;

    @Inject
    private EventService eventService;

    @GET
    @Path("/count")
    public Response count() {
        long count = service.count();
        return Response.ok(Map.of("count", count)).build();
    }

    @POST
    @Path("/save")
    public Response save(CoordinatesDTO coordinatesDTO) {
        CoordinatesDTO coordinates = service.save(coordinatesDTO);
        eventService.sendEvent("COORDINATES", "CREATED", coordinates);
        return Response.status(Response.Status.CREATED).build();
    }
}
