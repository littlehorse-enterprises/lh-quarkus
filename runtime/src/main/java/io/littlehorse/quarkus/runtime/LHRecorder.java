package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.recordable.LHWorkflowRecordable;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.usertask.UserTaskSchema;
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
            Class<?> classBean, String name, ShutdownContext shutdownContext) {
        Object bean = CDI.current().select(classBean).get();
        LHConfig config = CDI.current().select(LHConfig.class).get();

        LHTaskWorker worker = new LHTaskWorker(bean, name, config);
        shutdownContext.addShutdownTask(new CloseRunnable(worker));

        log.debug("Registering {}: {}", LHTaskMethod.class.getSimpleName(), name);
        worker.registerTaskDef();

        log.debug("Starting {}: {}", LHTaskMethod.class.getSimpleName(), name);
        worker.start();
    }

    public void registerLHWorkflow(LHWorkflowRecordable recordable) {
        LHWorkflowRegister register =
                CDI.current().select(LHWorkflowRegister.class).get();
        register.registerWorkflow(recordable.getWfSpecName(), recordable::getWorkflowThread);
    }

    public void registerLHUserTaskForm(Class<?> classBean, String name) {
        Object bean = CDI.current().select(classBean).get();
        LittleHorseBlockingStub stub =
                CDI.current().select(LittleHorseBlockingStub.class).get();

        UserTaskSchema schema = new UserTaskSchema(bean, name);

        log.debug("Registering {}: {}", LHUserTaskForm.class.getSimpleName(), name);
        stub.putUserTaskDef(schema.compile());
    }
}
