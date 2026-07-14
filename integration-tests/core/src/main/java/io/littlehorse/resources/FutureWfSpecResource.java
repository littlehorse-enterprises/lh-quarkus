package io.littlehorse.resources;

import io.littlehorse.sdk.common.proto.GetLatestWfSpecRequest;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseFutureStub;
import io.littlehorse.sdk.common.proto.WfSpec;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.concurrent.ExecutionException;

@Path("/future/wf-specs")
public class FutureWfSpecResource {

    private final LittleHorseFutureStub futureStub;

    public FutureWfSpecResource(LittleHorseFutureStub futureStub) {
        this.futureStub = futureStub;
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getWfSpecName(@PathParam("name") String name)
            throws ExecutionException, InterruptedException {
        WfSpec wfSpec = futureStub
                .getLatestWfSpec(
                        GetLatestWfSpecRequest.newBuilder().setName(name).build())
                .get();
        return wfSpec.getId().getName();
    }
}
