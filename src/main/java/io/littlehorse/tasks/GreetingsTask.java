package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.services.GreetingsService;

@LHTask
public class GreetingsTask {

    public static final String TASK_GREETINGS = "greetings";

    private final GreetingsService service;

    public GreetingsTask(GreetingsService service) {
        this.service = service;
    }

    @LHTaskMethod(TASK_GREETINGS)
    public String greetings(String name) {
        return service.sayHello(name);
    }
}
