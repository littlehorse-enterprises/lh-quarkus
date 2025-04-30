package io.littlehorse.workers;

import static io.littlehorse.configs.LittleHorseBeans.TASK_PRINT;

import io.littlehorse.sdk.worker.LHTaskMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrinterWorker {

    @LHTaskMethod(TASK_PRINT)
    public void greetings(String message) {
        log.info("Executing worker printer");
        System.out.println(message);
    }
}
