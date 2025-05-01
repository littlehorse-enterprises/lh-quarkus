package io.littlehorse.resources;

import io.littlehorse.services.GreetingService;
import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    private final GreetingService service;

    public GreetingResource(GreetingService service) {
        this.service = service;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@QueryParam("id") String id, @QueryParam("name") String name) {
        return service.runWf(id, name);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/reactive")
    public Uni<String> reactiveHello(@QueryParam("id") String id, @QueryParam("name") String name) {
        return service.runWfReactive(id, name);
    }
}
