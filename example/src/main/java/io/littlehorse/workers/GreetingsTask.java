package io.littlehorse.workers;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.services.GreetingsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LHTask
public class GreetingsTask {

    private static final Logger log = LoggerFactory.getLogger(GreetingsTask.class);

    public static final String TASK_COUNTER = "counter";
    public static final String TASK_GREETINGS = "greetings";

    private final GreetingsService service;
    private int count;

    public GreetingsTask(GreetingsService service) {
        this.service = service;
    }

    @LHTaskMethod(TASK_COUNTER)
    public int counter() {
        log.info("Executing worker counter");
        return ++count;
    }

    @LHTaskMethod(TASK_GREETINGS)
    public String greetings(String name) {
        log.info("Executing worker greetings");
        return service.sayHello(name);
    }
}
