package io.littlehorse.workers;

import static io.littlehorse.configs.LittleHorseBeans.TASK_PRINT;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LHTask
public class PrinterTask {

    private static final Logger log = LoggerFactory.getLogger(PrinterTask.class);

    @LHTaskMethod(TASK_PRINT)
    public void print(int totalCount, String message) {
        log.info("Executing worker printer");
        System.out.printf("Total count: %d\n%s\n", totalCount, message);
    }
}
