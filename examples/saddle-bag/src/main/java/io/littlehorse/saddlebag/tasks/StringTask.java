package io.littlehorse.saddlebag.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class StringTask {

    public static final String GREET = "${task.greet.name}";
    public static final String FORMAT_FULL_NAME = "${task.format-full-name.name}";

    @LHTaskMethod(value = GREET, description = "Prints a greeting message for the given name")
    public void greet(String name) {
        System.out.println("Hello, %s!".formatted(name));
    }

    @LHTaskMethod(
            value = FORMAT_FULL_NAME,
            description = "Formats first and last name into a full name")
    public String formatFullName(String firstName, String lastName) {
        return "%s %s".formatted(firstName, lastName);
    }
}
