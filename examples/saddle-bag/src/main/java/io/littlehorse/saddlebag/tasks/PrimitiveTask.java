package io.littlehorse.saddlebag.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class PrimitiveTask {

    public static final String ADD_NUMBERS = "${task.add-numbers.name}";
    public static final String IS_ELIGIBLE = "${task.is-eligible.name}";

    @LHTaskMethod(value = ADD_NUMBERS, description = "Adds two integers and returns their sum")
    public int addNumbers(int a, int b) {
        return a + b;
    }

    @LHTaskMethod(
            value = IS_ELIGIBLE,
            description = "Checks if a person is eligible based on age and license status")
    public boolean isEligible(int age, boolean hasLicense) {
        return age >= 18 && hasLicense;
    }
}
