package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutExternalEventDefRequest;
import io.littlehorse.sdk.common.proto.PutWorkflowEventDefRequest;
import io.littlehorse.sdk.wfsdk.ThreadFunc;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Unremovable
public class LHWorkflowRegister {

    private static final Logger log = LoggerFactory.getLogger(LHWorkflowRegister.class);
    private final LittleHorseBlockingStub blockingStub;

    public LHWorkflowRegister(LittleHorseBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    public void registerWorkflow(String name, ThreadFunc threadFunc) {
        Workflow workflow = Workflow.newWorkflow(name, threadFunc);
        workflow.getRequiredWorkflowEventDefNames().forEach(wfEvent -> {
            log.info("Registering WorkflowEvent: {}", wfEvent);
            PutWorkflowEventDefRequest request =
                    PutWorkflowEventDefRequest.newBuilder().setName(name).build();
            blockingStub.putWorkflowEventDef(request);
        });

        workflow.getRequiredExternalEventDefNames().forEach(exEvent -> {
            log.info("Registering ExternalEvent: {}", exEvent);
            PutExternalEventDefRequest request =
                    PutExternalEventDefRequest.newBuilder().setName(name).build();
            blockingStub.putExternalEventDef(request);
        });

        log.info("Registering {}: {}", LHWorkflow.class.getSimpleName(), name);
        workflow.registerWfSpec(blockingStub);
    }
}
