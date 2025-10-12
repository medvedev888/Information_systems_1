package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.vladislav.information_systems_1.dto.AddressDTO;
import me.vladislav.information_systems_1.service.AddressService;

import java.util.List;

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
        addressService.save(addressDTO);
        return Response.status(Response.Status.CREATED).build();
    }

    @PATCH
    public Response update(@Valid AddressDTO addressDTO) {
        addressService.update(addressDTO);
        return Response.noContent().build();

    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        addressService.delete(id);
        return Response.noContent().build();

    }
}
