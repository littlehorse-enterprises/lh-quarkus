package io.littlehorse.quarkus.proxy.resource.server;

import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/proxy/version")
public class ServerInformationResource {

    private final ServerInformationService service;

    public ServerInformationResource(ServerInformationService service) {
        this.service = service;
    }

    @GET
    @Tag(ref = "Version")
    public Uni<ServerInformationResponse> get() {
        return service.get();
    }
}
