package io.littlehorse.quarkus.proxy.resource.wfspec;

import io.littlehorse.sdk.common.proto.WfSpec;
import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/proxy/tenants/{tenant}/wf-specs")
public class WfSpecResource {

    private final WfSpecService service;

    public WfSpecResource(WfSpecService service) {
        this.service = service;
    }

    @GET
    @Path("/{wfSpecId}")
    public Uni<WfSpec> get(@PathParam("wfSpecId") String wfSpecId) {
        return service.get(wfSpecId);
    }
}
