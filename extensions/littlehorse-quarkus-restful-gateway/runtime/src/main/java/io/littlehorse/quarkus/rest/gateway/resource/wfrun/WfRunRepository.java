package io.littlehorse.quarkus.rest.gateway.resource.wfrun;

import io.littlehorse.quarkus.reactive.LittleHorseReactiveStub;
import io.littlehorse.quarkus.rest.gateway.context.TenantContext;
import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.ListVariablesRequest;
import io.littlehorse.sdk.common.proto.VariableList;
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

    public Uni<VariableList> variables(String id) {
        LittleHorseReactiveStub stub = context.getLittleHorseReactiveStub();
        ListVariablesRequest request = ListVariablesRequest.newBuilder()
                .setWfRunId(LHLibUtil.wfRunIdFromString(id))
                .build();
        return stub.getWfRun(LHLibUtil.wfRunIdFromString(id))
                .chain(() -> stub.listVariables(request));
    }

    public Uni<WfRun> get(String id) {
        return context.getLittleHorseReactiveStub().getWfRun(LHLibUtil.wfRunIdFromString(id));
    }
}
