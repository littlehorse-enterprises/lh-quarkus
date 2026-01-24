package io.littlehorse.quarkus.rest.gateway.resource.wfrun;

import io.littlehorse.quarkus.rest.gateway.context.TenantContext;
import io.littlehorse.sdk.common.proto.WfRun;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WfRunRepository {

    private final TenantContext context;

    public WfRunRepository(TenantContext context) {
        this.context = context;
    }

    public Uni<WfRun> run(WfRunRequest request) {
        return context.getLittleHorseReactiveStub().runWf(request.toProtobuf());
    }
}
