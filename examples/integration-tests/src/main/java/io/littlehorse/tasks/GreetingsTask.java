package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.services.GreetingsService;

@LHTask
public class GreetingsTask {

    public static final String GREETINGS_TASK = "greetings";

    private final GreetingsService service;

    public GreetingsTask(GreetingsService service) {
        this.service = service;
    }

    @LHTaskMethod(GREETINGS_TASK)
    public String greetings(String name) {
        return service.sayHello(name);
    }
}
