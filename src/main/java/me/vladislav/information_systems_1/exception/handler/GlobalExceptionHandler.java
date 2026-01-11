package me.vladislav.information_systems_1.exception.handler;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.vladislav.information_systems_1.exception.*;

import java.util.Map;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException runtimeException) {
        Throwable exception = unwrap(runtimeException);

        if (exception instanceof RelatedEntityDeletionException
                || exception instanceof IllegalArgumentException
                || exception instanceof ImportParseException
                || exception instanceof CoordinatesAlreadyUsedException
                || exception instanceof AddressAlreadyUsedException) {
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

        if (exception instanceof UserAlreadyExistException
                || exception instanceof ImportHistoryNotFoundException) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("success", false, "message", exception.getMessage()))
                    .build();
        }

        if (exception instanceof UserForbiddenException) {
            return Response.status(Response.Status.FORBIDDEN)
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

        if (exception instanceof MinioInitializationException
                || exception instanceof MinioUploadFileException
                || exception instanceof MinioDownloadException
                || exception instanceof ImportDatabaseException
                || exception instanceof ImportFileStorageException
        ) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("success", false, "message", exception.getMessage()))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "message", "Unexpected error"))
                .build();
    }

    private Throwable unwrap(Throwable t) {
        Throwable result = t;
        while (result.getCause() != null &&
                !(result instanceof MinioInitializationException)) {
            result = result.getCause();
        }
        return result;
    }
}
