package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.runtime.register.LHUserTaskRegister;
import io.littlehorse.sdk.common.proto.PutUserTaskDefRequest;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

public class LHUserTaskFormRecordable extends LHRecordable {

    @RecordableConstructor
    public LHUserTaskFormRecordable(Class<?> beanClass, String name) {
        super(beanClass, name);
    }

    public void registerUserTask() {
        if (!exists()) return;

        ConfigEvaluator configEvaluator = new ConfigEvaluator();
        LHUserTaskRegister register =
                CDI.current().select(LHUserTaskRegister.class).get();
        UserTaskSchema schema = new UserTaskSchema(
                getBeanClass(), configEvaluator.expand(getName()).asString());
        PutUserTaskDefRequest request = schema.compile();
        register.registerUserTask(request);
    }
}
