package io.littlehorse.quarkus.runtime.register;

import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutUserTaskDefRequest;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Unremovable
public class LHUserTaskRegister {

    private static final Logger log = LoggerFactory.getLogger(LHUserTaskRegister.class);
    private final LittleHorseBlockingStub blockingStub;
    private final LHRuntimeConfig config;

    public LHUserTaskRegister(LittleHorseBlockingStub blockingStub, LHRuntimeConfig config) {
        this.blockingStub = blockingStub;
        this.config = config;
    }

    public void registerUserTask(Object bean, String name) {
        if (!config.userTaskRegisterEnabled()) return;

        UserTaskSchema schema = new UserTaskSchema(bean, name);
        PutUserTaskDefRequest request = schema.compile();
        log.info("Registering {}: {}", LHUserTaskForm.class.getSimpleName(), name);
        blockingStub.putUserTaskDef(request);
    }
}
