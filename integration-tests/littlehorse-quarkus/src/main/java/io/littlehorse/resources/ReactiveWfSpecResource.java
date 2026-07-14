package io.littlehorse.resources;

import io.littlehorse.quarkus.reactive.LittleHorseReactiveStub;
import io.littlehorse.sdk.common.proto.GetLatestWfSpecRequest;
import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/reactive/wf-specs")
public class ReactiveWfSpecResource {

    private final LittleHorseReactiveStub reactiveStub;

    public ReactiveWfSpecResource(LittleHorseReactiveStub reactiveStub) {
        this.reactiveStub = reactiveStub;
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> getWfSpecName(@PathParam("name") String name) {
        return reactiveStub
                .getLatestWfSpec(
                        GetLatestWfSpecRequest.newBuilder().setName(name).build())
                .map(wfSpec -> wfSpec.getId().getName());
    }
}
