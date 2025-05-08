package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHUserTaskRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowRecordable;
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
            LHTaskMethodRecordable recordable, ShutdownContext shutdownContext) {
        LHTaskWorker worker = recordable.initTaskWorker();
        shutdownContext.addShutdownTask(new CloseRunnable(worker));

        log.debug(
                "Registering {}: {}", LHTaskMethod.class.getSimpleName(), worker.getTaskDefName());
        worker.registerTaskDef();

        log.debug("Starting {}: {}", LHTaskMethod.class.getSimpleName(), worker.getTaskDefName());
        worker.start();
    }

    public void registerLHWorkflow(LHWorkflowRecordable recordable) {
        LHWorkflowRegister register =
                CDI.current().select(LHWorkflowRegister.class).get();
        register.registerWorkflow(recordable.getWfSpecName(), recordable::buildWorkflowThread);
    }

    public void registerLHUserTaskForm(LHUserTaskRecordable recordable) {
        LHUserTaskRegister register =
                CDI.current().select(LHUserTaskRegister.class).get();
        register.registerUserTask(recordable.getBean(), recordable.getUserTaskDefName());
    }
}
