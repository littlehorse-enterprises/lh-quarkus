package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutExternalEventDefRequest;
import io.littlehorse.sdk.common.proto.PutWorkflowEventDefRequest;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import io.littlehorse.sdk.wfsdk.ThreadFunc;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskMethod;
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
            String name, Class<?> classBean, ShutdownContext shutdownContext) {
        Object bean = CDI.current().select(classBean).get();
        LHConfig config = CDI.current().select(LHConfig.class).get();

        LHTaskWorker worker = new LHTaskWorker(bean, name, config);
        shutdownContext.addShutdownTask(new CloseRunnable(worker));

        log.info("Registering {}: {}", LHTaskMethod.class.getSimpleName(), name);
        worker.registerTaskDef();

        log.info("Starting {}: {}", LHTaskMethod.class.getSimpleName(), name);
        worker.start();
    }

    public void registerLHWorkflow(String name, Class<?> classBean) {
        ThreadFunc bean = (ThreadFunc) CDI.current().select(classBean).get();
        LittleHorseBlockingStub stub =
                CDI.current().select(LittleHorseBlockingStub.class).get();

        Workflow workflow = Workflow.newWorkflow(name, bean);

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

        log.info("Registering {}: {}", LHWorkflow.class.getSimpleName(), name);
        workflow.registerWfSpec(stub);
    }

    public void registerLHUserTaskForm(String name, Class<?> classBean) {
        Object bean = CDI.current().select(classBean).get();
        LittleHorseBlockingStub stub =
                CDI.current().select(LittleHorseBlockingStub.class).get();

        UserTaskSchema schema = new UserTaskSchema(bean, name);

        log.info("Registering {}: {}", LHUserTaskForm.class.getSimpleName(), name);
        stub.putUserTaskDef(schema.compile());
    }
}
