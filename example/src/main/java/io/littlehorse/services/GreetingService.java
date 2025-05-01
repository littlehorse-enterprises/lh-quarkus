package io.littlehorse.services;

import static io.littlehorse.configs.LittleHorseBeans.TASK_GREETINGS;
import static io.littlehorse.configs.LittleHorseBeans.VAR_NAME;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService {

    private final LittleHorseGrpc.LittleHorseBlockingStub blockingStub;
    private final LittleHorseGrpc.LittleHorseFutureStub futureStub;

    public GreetingService(
            LittleHorseGrpc.LittleHorseBlockingStub blockingStub,
            LittleHorseGrpc.LittleHorseFutureStub futureStub) {
        this.blockingStub = blockingStub;
        this.futureStub = futureStub;
    }

    private static RunWfRequest newWfRunRequest(String id, String name) {
        return RunWfRequest.newBuilder()
                .setId(id)
                .setWfSpecName(TASK_GREETINGS)
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
}
