package io.littlehorse.quarkus.runtime.register;

import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
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
    private final LHRuntimeConfig config;

    public LHWorkflowRegister(LittleHorseBlockingStub blockingStub, LHRuntimeConfig config) {
        this.blockingStub = blockingStub;
        this.config = config;
    }

    public void registerWorkflow(String name, ThreadFunc threadFunc) {
        if (!config.workflowsRegisterEnabled()) return;

        Workflow workflow = Workflow.newWorkflow(name, threadFunc);

        log.info("Registering {}: {}", LHWorkflow.class.getSimpleName(), name);
        workflow.registerWfSpec(blockingStub);
    }
}
