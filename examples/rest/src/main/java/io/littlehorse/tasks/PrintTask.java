package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class PrintTask {

    public static final String PRINT_TASK = "print";

    @LHTaskMethod(PRINT_TASK)
    public void print(String message) {
        System.out.println(message);
    }
}
