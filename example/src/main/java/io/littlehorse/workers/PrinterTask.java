package io.littlehorse.workers;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LHTask
public class PrinterTask {

    private static final Logger log = LoggerFactory.getLogger(PrinterTask.class);

    public static final String TASK_PRINT = "print";

    @LHTaskMethod(TASK_PRINT)
    public void print(int totalCount, String message) {
        log.info("Executing worker printer");
        System.out.printf("Total count: %d\n%s\n", totalCount, message);
    }
}
