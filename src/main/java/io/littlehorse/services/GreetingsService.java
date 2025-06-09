package io.littlehorse.services;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.workflows.GreetingsWorkflow;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@ApplicationScoped
public class GreetingsService {

    private final LittleHorseBlockingStub blockingStub;

    public GreetingsService(LittleHorseBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    public String runWf(String id, String name) {
        RunWfRequest request = RunWfRequest.newBuilder()
                .setWfSpecName(GreetingsWorkflow.GREETINGS_WORKFLOW)
                .putVariables(GreetingsWorkflow.NAME_VARIABLE, LHLibUtil.objToVarVal(name))
                .setId(StringUtils.isBlank(id) ? UUID.randomUUID().toString().replace("-", "") : id)
                .build();
        return blockingStub.runWf(request).getId().getId();
    }

    public String sayHello(String name) {
        return "Hello %s!".formatted(name);
    }
}
