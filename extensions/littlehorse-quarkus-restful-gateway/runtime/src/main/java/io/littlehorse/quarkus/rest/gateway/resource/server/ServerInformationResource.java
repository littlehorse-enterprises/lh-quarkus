package io.littlehorse.quarkus.rest.gateway.resource.server;

import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gateway/version")
public class ServerInformationResource {

    private final ServerInformationRepository repository;

    public ServerInformationResource(ServerInformationRepository repository) {
        this.repository = repository;
    }

    @GET
    @APIResponse(responseCode = "200", description = "Record retrieved successfully")
    @APIResponse(responseCode = "500", description = "Internal error")
    @Tag(ref = "Version")
    public Uni<ServerInformationResponse> get() {
        return repository.get();
    }
}
