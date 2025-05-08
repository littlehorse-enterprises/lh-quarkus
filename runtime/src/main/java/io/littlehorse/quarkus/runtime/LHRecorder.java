package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHUserTaskRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowRecordable;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

import jakarta.enterprise.inject.spi.CDI;

@Recorder
public class LHRecorder {

    public void startLHTaskMethod(
            LHTaskMethodRecordable recordable, ShutdownContext shutdownContext) {
        recordable.registerAndStartTaskWorker(shutdownContext);
    }

    public void registerLHWorkflow(LHWorkflowRecordable recordable) {
        LHWorkflowRegister register =
                CDI.current().select(LHWorkflowRegister.class).get();
        register.registerWorkflow(recordable.getWfSpecName(), recordable::buildWorkflowThread);
    }

    public void registerLHUserTaskForm(LHUserTaskRecordable recordable) {
        recordable.registerUserTask();
    }
}
