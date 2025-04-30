package io.littlehorse.workers;

import static io.littlehorse.configs.LittleHorseBeans.TASK_GREETINGS;

import io.littlehorse.sdk.worker.LHTaskMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GreetingWorker {

    @LHTaskMethod(TASK_GREETINGS)
    public String greetings(String name) {
        log.info("Executing worker greetings");
        return "Hello there! " + name;
    }
}
