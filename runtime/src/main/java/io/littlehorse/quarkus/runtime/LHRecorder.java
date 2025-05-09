package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHUserTaskRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowRecordable;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class LHRecorder {

    public void registerAndStartTaskWorker(
            LHTaskMethodRecordable recordable, ShutdownContext shutdownContext) {
        recordable.registerAndStartTaskWorker(shutdownContext);
    }

    public void registerLHWorkflow(LHWorkflowRecordable recordable) {
        recordable.registerWorkflow();
    }

    public void registerLHUserTaskForm(LHUserTaskRecordable recordable) {
        recordable.registerUserTask();
    }
}
