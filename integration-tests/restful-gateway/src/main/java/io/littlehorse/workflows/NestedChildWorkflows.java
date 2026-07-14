package io.littlehorse.workflows;

import static io.littlehorse.tasks.GreetingsTask.GREETINGS_TASK;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.SpawnedChildWf;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;

@ApplicationScoped
public class NestedChildWorkflows {

    public static final String GRANDPARENT_WF = "nested-grandparent-wf";
    public static final String PARENT_WF = "nested-parent-wf";
    public static final String CHILD_WF = "nested-child-wf";

    @LHWorkflow(GRANDPARENT_WF)
    public void grandparent(WorkflowThread wf) {
        SpawnedChildWf childRun = wf.runWf(PARENT_WF, Map.of());
        wf.waitForChildWf(childRun);
    }

    @LHWorkflow(PARENT_WF)
    public void parent(WorkflowThread wf) {
        SpawnedChildWf childRun = wf.runWf(CHILD_WF, Map.of());
        wf.waitForChildWf(childRun);
    }

    @LHWorkflow(CHILD_WF)
    public void child(WorkflowThread wf) {
        wf.execute(GREETINGS_TASK, "nested-child");
    }
}
