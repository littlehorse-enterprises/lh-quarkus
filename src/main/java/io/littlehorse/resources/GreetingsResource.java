package io.littlehorse.resources;

import io.littlehorse.services.GreetingsService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingsResource {

    private final GreetingsService service;

    public GreetingsResource(GreetingsService service) {
        this.service = service;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@QueryParam("id") String id, @QueryParam("name") String name) {
        return service.runWf(id, name);
    }
}
