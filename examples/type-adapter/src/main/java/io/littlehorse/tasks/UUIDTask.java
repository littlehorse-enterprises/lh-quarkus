package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;
import io.littlehorse.structs.UUIDContainer;

import java.util.UUID;

@LHTask
public class UUIDTask {

    public static final String EXAMPLE_TYPE_ADAPTER = "example-type-adapter";
    public static final String UUID_VAR = "uuid";
    public static final String UUID_CONTAINER_VAR = "uuid-container";
    public static final String GET_UUID_TASK = "get-uuid";
    public static final String ECHO_UUID_TASK = "echo-uuid";

    @LHWorkflow(EXAMPLE_TYPE_ADAPTER)
    public void greetingsWorkflow(WorkflowThread wf) {
        WfRunVariable uuidVar = wf.declareStr(UUID_VAR).searchable();
        uuidVar.assign(wf.execute(GET_UUID_TASK));

        WfRunVariable containerVar = wf.declareStruct(UUID_CONTAINER_VAR, UUIDContainer.class);
        containerVar.assign(wf.execute(ECHO_UUID_TASK, uuidVar));
    }

    @LHTaskMethod(value = GET_UUID_TASK, description = "Generates and returns a random UUID.")
    public UUID getUUID() {
        return UUID.randomUUID();
    }

    @LHTaskMethod(
            value = ECHO_UUID_TASK,
            description = "Receives a UUID and returns a UUIDContainer with a message.")
    public UUIDContainer echoUUID(UUID uuid, WorkerContext context) {
        String message = "Received UUID via adapter: " + uuid;
        context.log(message);
        System.out.println(message);
        return new UUIDContainer(uuid, message);
    }
}
