package io.littlehorse.resources;

import io.littlehorse.services.ReactiveService;
import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/print")
public class ReactiveResource {

    private final ReactiveService service;

    public ReactiveResource(ReactiveService service) {
        this.service = service;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> print(@QueryParam("id") String id, @QueryParam("message") String message) {
        return service.runWf(id, message);
    }
}
