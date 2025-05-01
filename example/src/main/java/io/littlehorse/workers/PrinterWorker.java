package io.littlehorse.workers;

import static io.littlehorse.configs.LittleHorseBeans.TASK_PRINT;

import io.littlehorse.sdk.worker.LHTaskMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrinterWorker {

    private static final Logger log = LoggerFactory.getLogger(PrinterWorker.class);

    @LHTaskMethod(TASK_PRINT)
    public void greetings(String message) {
        log.info("Executing worker printer");
        System.out.println(message);
    }
}
