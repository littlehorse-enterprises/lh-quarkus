package io.littlehorse.workers;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class PrinterTask {

    public static final String TASK_PRINT = "print";

    @LHTaskMethod(TASK_PRINT)
    public void print(String message) {
        System.out.println(message);
    }
}
