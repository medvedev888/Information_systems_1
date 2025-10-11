package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.CoordinatesDTO;
import me.vladislav.information_systems_1.exception.CoordinatesNotFoundException;
import me.vladislav.information_systems_1.service.CoordinatesService;

import java.util.List;
import java.util.Map;

@Path("/coordinates")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CoordinatesController {
    @Inject
    private CoordinatesService coordinatesService;

    @GET
    public Response getPage(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        List<CoordinatesDTO> coordinatesList = coordinatesService.getPage(page, size);
        return Response.ok(coordinatesList).build();
    }

    @POST
    public Response create(@Valid CoordinatesDTO coordinatesDTO) {
        try {
            coordinatesService.save(coordinatesDTO);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Could not create coordinates"))
                    .build();
        }
    }

    @PATCH
    public Response update(@Valid CoordinatesDTO coordinatesDTO) {
        try {
            coordinatesService.update(coordinatesDTO);
            return Response.noContent().build();
        } catch (CoordinatesNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("success", false, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Could not update coordinates"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            coordinatesService.delete(id);
            return Response.noContent().build();
        } catch (CoordinatesNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("success", false, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Could not delete coordinates"))
                    .build();
        }
    }
}
