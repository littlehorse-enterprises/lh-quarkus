package io.littlehorse.workflows;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;

import java.util.Map;

@LHTask
public class ArraysMapsWorkflow {

    public static final String ARRAYS_MAPS_WF = "arrays-maps";
    public static final String NUMBERS_VAR = "numbers";
    public static final String COUNTS_VAR = "counts";
    public static final String SUM_ARRAY_TASK = "sum-array";
    public static final String COUNT_MAP_TASK = "count-map";

    @LHWorkflow(ARRAYS_MAPS_WF)
    public void workflow(WorkflowThread wf) {
        WfRunVariable numbers =
                wf.declareArray(NUMBERS_VAR, Long.class).withDefault(new Long[] {1L, 2L, 3L});
        WfRunVariable counts = wf.declareMap(COUNTS_VAR, String.class, Long.class)
                .withDefault(Map.of("apples", 3L, "bananas", 5L));

        wf.execute(SUM_ARRAY_TASK, numbers);
        wf.execute(COUNT_MAP_TASK, counts);
    }

    @LHTaskMethod(SUM_ARRAY_TASK)
    public long sumArray(@LHType(isLHArray = true) Long[] numbers) {
        long total = 0;
        for (Long number : numbers) {
            total += number;
        }
        return total;
    }

    @LHTaskMethod(COUNT_MAP_TASK)
    public int countMap(@LHType(isLHMap = true) Map<String, Long> counts) {
        return counts.size();
    }
}
