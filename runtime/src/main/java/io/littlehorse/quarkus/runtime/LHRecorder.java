package io.littlehorse.quarkus.runtime;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutExternalEventDefRequest;
import io.littlehorse.sdk.common.proto.PutWorkflowEventDefRequest;
import io.littlehorse.sdk.wfsdk.ThreadFunc;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.ShutdownContext.CloseRunnable;
import io.quarkus.runtime.annotations.Recorder;

import jakarta.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Recorder
public class LHRecorder {
    private static final Logger log = LoggerFactory.getLogger(LHRecorder.class);

    public void startLHTaskMethod(
            String name, Class<?> executableClass, ShutdownContext shutdownContext) {
        Object executableBean = CDI.current().select(executableClass).get();
        LHConfig config = CDI.current().select(LHConfig.class).get();
        LHTaskWorker worker = new LHTaskWorker(executableBean, name, config);
        shutdownContext.addShutdownTask(new CloseRunnable(worker));
        log.info("Registering LHTaskMethod: {}", name);
        worker.registerTaskDef();
        log.info("Starting LHTaskMethod: {}", name);
        worker.start();
    }

    public void registerLHWorkflow(String name, Class<?> clazz) {
        ThreadFunc workflowBean = (ThreadFunc) CDI.current().select(clazz).get();
        LittleHorseBlockingStub stub =
                CDI.current().select(LittleHorseBlockingStub.class).get();
        Workflow workflow = Workflow.newWorkflow(name, workflowBean);

        workflow.getRequiredWorkflowEventDefNames().forEach(wfEvent -> {
            log.info("Registering WorkflowEvent: {}", wfEvent);
            stub.putWorkflowEventDef(
                    PutWorkflowEventDefRequest.newBuilder().setName(name).build());
        });

        workflow.getRequiredExternalEventDefNames().forEach(exEvent -> {
            log.info("Registering ExternalEvent: {}", exEvent);
            stub.putExternalEventDef(
                    PutExternalEventDefRequest.newBuilder().setName(name).build());
        });

        log.info("Registering LHWorkflow: {}", name);
        workflow.registerWfSpec(stub);
    }
}
