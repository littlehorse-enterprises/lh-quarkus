package io.littlehorse.services;

import static io.littlehorse.workflows.GreetingsWorkflow.VAR_NAME;
import static io.littlehorse.workflows.GreetingsWorkflow.WF_GREETINGS;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseFutureStub;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingsService {

    private final LittleHorseBlockingStub blockingStub;
    private final LittleHorseFutureStub futureStub;

    public GreetingsService(
            LittleHorseBlockingStub blockingStub, LittleHorseFutureStub futureStub) {
        this.blockingStub = blockingStub;
        this.futureStub = futureStub;
    }

    private static RunWfRequest newWfRunRequest(String id, String name) {
        return RunWfRequest.newBuilder()
                .setId(id)
                .setWfSpecName(WF_GREETINGS)
                .putVariables(VAR_NAME, LHLibUtil.objToVarVal(name))
                .build();
    }

    public String runWf(String id, String name) {
        return blockingStub.runWf(newWfRunRequest(id, name)).getId().getId();
    }

    public Uni<String> runWfReactive(String id, String name) {
        return Uni.createFrom()
                .future(futureStub.runWf(newWfRunRequest(id, name)))
                .map(wfRun -> wfRun.getId().getId());
    }

    public String sayHello(String name) {
        return "Hello there! " + name;
    }
}
