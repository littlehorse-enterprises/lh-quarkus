package io.littlehorse.saddlebag.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class StringTask {

    public static final String GREET = "${task.greet.name}";
    public static final String FORMAT_FULL_NAME = "${task.format-full-name.name}";

    @LHTaskMethod(GREET)
    public String greet(String name) {
        return "Hello, %s!".formatted(name);
    }

    @LHTaskMethod(FORMAT_FULL_NAME)
    public String formatFullName(String firstName, String lastName) {
        return "%s %s".formatted(firstName, lastName);
    }
}
