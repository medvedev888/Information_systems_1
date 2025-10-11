package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.AddressDTO;
import me.vladislav.information_systems_1.exception.AddressNotFoundException;
import me.vladislav.information_systems_1.exception.LocationNotFoundException;
import me.vladislav.information_systems_1.service.AddressService;

import java.util.List;
import java.util.Map;

@Path("/addresses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AddressController {

    @Inject
    private AddressService addressService;

    @GET
    public Response getPage(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        List<AddressDTO> addresses = addressService.getPage(page, size);
        return Response.ok(addresses).build();
    }

    @POST
    public Response create(@Valid AddressDTO addressDTO) {
        try {
            addressService.save(addressDTO);
            return Response.status(Response.Status.CREATED).build();
        } catch (LocationNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("success", false, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Could not create address"))
                    .build();
        }
    }

    @PATCH
    public Response update(@Valid AddressDTO addressDTO) {
        try {
            addressService.update(addressDTO);
            return Response.noContent().build();
        } catch (AddressNotFoundException | LocationNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("success", false, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Could not update address"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            addressService.delete(id);
            return Response.noContent().build();
        } catch (AddressNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("success", false, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", "Could not delete address"))
                    .build();
        }
    }
}
