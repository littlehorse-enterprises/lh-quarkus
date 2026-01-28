package io.littlehorse.quarkus.rest.gateway.resource.externalevent;

import io.littlehorse.quarkus.rest.gateway.context.TenantContext;
import io.littlehorse.sdk.common.proto.ExternalEvent;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExternalEventRepository {

    private final TenantContext context;

    public ExternalEventRepository(TenantContext context) {
        this.context = context;
    }

    public Uni<ExternalEvent> post(ExternalEventRequest request) {
        return context.getLittleHorseReactiveStub().putExternalEvent(request.toProtobuf());
    }
}
