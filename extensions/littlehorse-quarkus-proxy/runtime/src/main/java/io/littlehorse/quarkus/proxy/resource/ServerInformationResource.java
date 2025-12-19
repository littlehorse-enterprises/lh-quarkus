package io.littlehorse.quarkus.proxy.resource;

import io.littlehorse.quarkus.proxy.presentation.ServerInformationResponse;
import io.littlehorse.quarkus.proxy.service.ServerInformationService;
import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/proxy/server")
public class ServerInformationResource {

    private final ServerInformationService service;

    public ServerInformationResource(ServerInformationService service) {
        this.service = service;
    }

    @GET
    public Uni<ServerInformationResponse> get() {
        return service.get();
    }
}
