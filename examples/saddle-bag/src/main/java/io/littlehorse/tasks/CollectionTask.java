package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;

import java.util.Map;

@LHTask
public class CollectionTask {

    public static final String SUM_NUMBERS = "${task.sum-numbers.name}";
    public static final String COUNT_ITEMS = "${task.count-items.name}";
    public static final String LIST_TAGS = "${task.list-tags.name}";

    @LHTaskMethod(value = SUM_NUMBERS, description = "Sums a native Array of integers")
    public long sumNumbers(@LHType(isLHArray = true) Long[] numbers) {
        long total = 0;
        for (Long number : numbers) {
            total += number;
        }
        return total;
    }

    @LHTaskMethod(value = COUNT_ITEMS, description = "Counts the entries in a native Map")
    public int countItems(@LHType(isLHMap = true) Map<String, Long> items) {
        return items.size();
    }

    @LHTaskMethod(value = LIST_TAGS, description = "Returns a native Array of tag names")
    @LHType(isLHArray = true)
    public String[] listTags() {
        return new String[] {"a", "b", "c"};
    }
}
