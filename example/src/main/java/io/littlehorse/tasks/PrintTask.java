package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class PrintTask {

    public static final String TASK_PRINT = "print";

    @LHTaskMethod(TASK_PRINT)
    public void print(String message) {
        System.out.println(message);
    }
}
