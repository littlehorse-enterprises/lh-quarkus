package io.littlehorse.arraysmaps;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;

import java.util.List;
import java.util.Map;

/**
 * Demonstrates LittleHorse native {@code Array} and {@code Map} types in a Quarkus task worker.
 *
 * <p>Array and Map support is inherited from the LittleHorse Java SDK — task parameters and return
 * types are marked with {@code @LHType(isLHArray = true)} / {@code @LHType(isLHMap = true)}, and
 * workflow variables are declared with {@code declareArray} / {@code declareMap}.
 */
@LHTask
public class ArraysMapsWorkflow {

    public static final String ARRAYS_MAPS_WF = "arrays-maps";
    public static final String NUMBERS_VAR = "numbers";
    public static final String COUNTS_VAR = "counts";
    public static final String PRODUCE_ARRAY_TASK = "produce-array";
    public static final String SUM_ARRAY_TASK = "sum-array";
    public static final String COUNT_MAP_TASK = "count-map";

    @LHWorkflow(ARRAYS_MAPS_WF)
    public void workflow(WorkflowThread wf) {
        WfRunVariable numbers =
                wf.declareArray(NUMBERS_VAR, Long.class).withDefault(new Long[] {1L, 2L, 3L});
        WfRunVariable counts = wf.declareMap(COUNTS_VAR, String.class, Long.class)
                .withDefault(Map.of("apples", 3L, "bananas", 5L));

        // Concatenate the Array produced by a task with EXTEND, then sum the result.
        numbers.assign(numbers.extend(wf.execute(PRODUCE_ARRAY_TASK)));
        wf.execute(SUM_ARRAY_TASK, numbers);

        // Consume the Map.
        wf.execute(COUNT_MAP_TASK, counts);
    }

    @LHTaskMethod(PRODUCE_ARRAY_TASK)
    @LHType(isLHArray = true)
    public Long[] produceArray() {
        return new Long[] {10L, 20L};
    }

    @LHTaskMethod(SUM_ARRAY_TASK)
    public long sumArray(@LHType(isLHArray = true) Long[] numbers) {
        long total = 0;
        for (Long number : numbers) {
            total += number;
        }
        System.out.printf("Summed Array %s = %d%n", List.of(numbers), total);
        return total;
    }

    @LHTaskMethod(COUNT_MAP_TASK)
    public int countMap(@LHType(isLHMap = true) Map<String, Long> counts) {
        System.out.printf("Consuming Map %s%n", counts);
        return counts.size();
    }
}
