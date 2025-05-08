package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc;
import io.littlehorse.sdk.common.proto.PutUserTaskDefRequest;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Unremovable
public class LHUserTaskRegister {

    private static final Logger log = LoggerFactory.getLogger(LHWorkflowRegister.class);
    private final LittleHorseGrpc.LittleHorseBlockingStub blockingStub;

    public LHUserTaskRegister(LittleHorseGrpc.LittleHorseBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    public void registerUserTask(Object bean, String name) {
        UserTaskSchema schema = new UserTaskSchema(bean, name);
        PutUserTaskDefRequest request = schema.compile();
        log.info("Registering {}: {}", LHUserTaskForm.class.getSimpleName(), name);
        blockingStub.putUserTaskDef(request);
    }
}
