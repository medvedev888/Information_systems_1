package me.vladislav.information_systems_1.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import me.vladislav.information_systems_1.dto.ImportHistoryDTO;
import me.vladislav.information_systems_1.dto.PageResponse;
import me.vladislav.information_systems_1.service.ImportHistoryService;

import java.io.InputStream;


@Path("/imports")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ImportHistoryController {

    @Context
    private SecurityContext securityContext;

    @Inject
    private ImportHistoryService importHistoryService;

    @GET
    public Response getPage(@QueryParam("page") Integer pageParam,
                            @QueryParam("size") Integer sizeParam,
                            @QueryParam("filterField") String filterFieldParam,
                            @QueryParam("filterValue") String filterValueParam,
                            @QueryParam("sortField") String sortFieldParam,
                            @QueryParam("sortOrder") String sortOrderParam) {
        int page = (pageParam == null || pageParam < 1) ? 1 : pageParam;
        int size = (sizeParam == null || sizeParam < 1) ? 10 : sizeParam;
        String filterField = (filterFieldParam == null) ? null : filterFieldParam.trim();
        String filterValue = (filterValueParam == null) ? null : filterValueParam.trim();
        String sortField = (sortFieldParam == null) ? null : sortFieldParam.trim();
        String sortOrder = (sortOrderParam == null) ? "asc" : sortOrderParam.trim().toLowerCase();

        PageResponse<ImportHistoryDTO> response;
        if(securityContext.isUserInRole("ADMIN")) {
            response = importHistoryService.getPage(page, size, filterField, filterValue, sortField, sortOrder, null);
        } else {
            response = importHistoryService.getPage(page, size, filterField, filterValue, sortField, sortOrder, securityContext.getUserPrincipal().getName());
        }
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}/file")
    public Response downloadImportFile(@PathParam("id") Long importId) {

        InputStream stream = importHistoryService.downloadImportFile(importId);

        return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        "Content-Disposition",
                        "attachment; filename=\"import_" + importId + ".txt\""
                )
                .build();
    }

}
