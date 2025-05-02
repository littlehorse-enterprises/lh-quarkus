package io.littlehorse.workers;

import static io.littlehorse.configs.LittleHorseBeans.TASK_GREETINGS;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LHTask
public class GreetingsTask {

    private static final Logger log = LoggerFactory.getLogger(GreetingsTask.class);

    @LHTaskMethod(TASK_GREETINGS)
    public String greetings(String name) {
        log.info("Executing worker greetings");
        return "Hello there! " + name;
    }
}
