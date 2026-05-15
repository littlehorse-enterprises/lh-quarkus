package io.littlehorse.saddlebag.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class PrimitiveTask {

    public static final String ADD_NUMBERS = "${task.add-numbers.name}";
    public static final String IS_ELIGIBLE = "${task.is-eligible.name}";

    @LHTaskMethod(ADD_NUMBERS)
    public int addNumbers(int a, int b) {
        return a + b;
    }

    @LHTaskMethod(IS_ELIGIBLE)
    public boolean isEligible(int age, boolean hasLicense) {
        return age >= 18 && hasLicense;
    }
}
