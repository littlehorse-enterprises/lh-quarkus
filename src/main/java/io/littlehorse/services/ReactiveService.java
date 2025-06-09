package io.littlehorse.services;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.AwaitWorkflowEventRequest;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseFutureStub;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.WorkflowEventDefId;
import io.littlehorse.workflows.ReactiveWorkflow;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@ApplicationScoped
public class ReactiveService {
    private final LittleHorseFutureStub futureStub;

    public ReactiveService(LittleHorseFutureStub futureStub) {
        this.futureStub = futureStub;
    }

    public Uni<String> runWf(String id, String message) {
        String wfRunId =
                StringUtils.isBlank(id) ? UUID.randomUUID().toString().replace("-", "") : id;

        RunWfRequest request = RunWfRequest.newBuilder()
                .setWfSpecName(ReactiveWorkflow.REACTIVE_WORKFLOW)
                .putVariables(ReactiveWorkflow.MESSAGE_VARIABLE, LHLibUtil.objToVarVal(message))
                .setId(wfRunId)
                .build();

        AwaitWorkflowEventRequest awaitEvent = AwaitWorkflowEventRequest.newBuilder()
                .addEventDefIds(
                        WorkflowEventDefId.newBuilder().setName(ReactiveWorkflow.NOTIFY_EVENT))
                .setWfRunId(LHLibUtil.wfRunIdFromString(wfRunId))
                .build();

        return Uni.createFrom()
                .future(futureStub.runWf(request))
                .onItem()
                .transformToUni(requestAwait ->
                        Uni.createFrom().future(futureStub.awaitWorkflowEvent(awaitEvent)))
                .map(wfEvent -> wfEvent.getContent().getStr());
    }
}
