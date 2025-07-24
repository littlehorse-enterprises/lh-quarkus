package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class GreetingsTask {

    public static final String GREETINGS_TASK = "greetings";

    @LHTaskMethod(GREETINGS_TASK)
    public String greetings(String name) {
        return "Hello %s".formatted(name);
    }
}
