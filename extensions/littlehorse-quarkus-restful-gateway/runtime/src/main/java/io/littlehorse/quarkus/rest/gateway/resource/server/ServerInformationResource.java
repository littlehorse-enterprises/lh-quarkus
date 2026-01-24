package io.littlehorse.quarkus.rest.gateway.resource.server;

import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gateway/version")
public class ServerInformationResource {

    private final ServerInformationRepository repository;

    public ServerInformationResource(ServerInformationRepository repository) {
        this.repository = repository;
    }

    @GET
    @Tag(ref = "Version")
    public Uni<ServerInformationResponse> get() {
        return repository.get();
    }
}
