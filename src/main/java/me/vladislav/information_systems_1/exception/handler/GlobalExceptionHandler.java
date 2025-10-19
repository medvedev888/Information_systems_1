package me.vladislav.information_systems_1.exception.handler;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.vladislav.information_systems_1.exception.*;

import java.util.Map;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {

        if (exception instanceof RelatedEntityDeletionException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("success", false, "message", exception.getMessage()))
                    .build();
        }

        if (exception instanceof InvalidCredentialsException
                || exception instanceof UserNotFoundException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("success", false, "message", exception.getMessage()))
                    .build();
        }

        if (exception instanceof UserAlreadyExistException) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("success", false, "message", exception.getMessage()))
                    .build();
        }

        if (exception instanceof AddressNotFoundException
                || exception instanceof LocationNotFoundException
                || exception instanceof CoordinatesNotFoundException
                || exception instanceof OrganizationNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("success", false, "message", exception.getMessage()))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "message", "Unexpected error"))
                .build();
    }
}
