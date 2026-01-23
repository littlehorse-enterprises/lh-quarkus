package io.littlehorse.quarkus.rest.gateway.infrastructure;

import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GrpcExceptionMapper implements ExceptionMapper<StatusRuntimeException> {

    private record ErrorEventMessage(Code code, String message) {}

    @Override
    public Response toResponse(StatusRuntimeException exception) {
        return switch (exception.getStatus().getCode()) {
            case NOT_FOUND -> Response.status(Status.NOT_FOUND).build();
            case UNAUTHENTICATED -> Response.status(Status.UNAUTHORIZED).build();
            case INVALID_ARGUMENT -> Response.status(Status.BAD_REQUEST).build();
            case PERMISSION_DENIED -> Response.status(Status.FORBIDDEN).build();
            case ALREADY_EXISTS -> Response.status(Status.CONFLICT).build();
            default ->
                Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorEventMessage(
                                exception.getStatus().getCode(), exception.getMessage()))
                        .build();
        };
    }
}
