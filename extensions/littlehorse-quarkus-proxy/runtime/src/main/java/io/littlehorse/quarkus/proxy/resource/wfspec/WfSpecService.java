package io.littlehorse.quarkus.proxy.resource.wfspec;

import io.littlehorse.quarkus.proxy.context.TenantContext;
import io.littlehorse.sdk.common.proto.WfSpec;
import io.littlehorse.sdk.common.proto.WfSpecId;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WfSpecService {

    private final TenantContext context;

    public WfSpecService(TenantContext context) {
        this.context = context;
    }

    public Uni<WfSpec> get(String wfSpecId) {
        return context.getLittleHorseReactiveStub()
                .getWfSpec(WfSpecId.newBuilder().setName(wfSpecId).build());
    }
}
