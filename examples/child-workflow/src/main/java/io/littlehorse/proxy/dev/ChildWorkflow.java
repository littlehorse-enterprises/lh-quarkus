package io.littlehorse.proxy.dev;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class ChildWorkflow {
    public static final String GREETINGS_TASK = "child-wf-greetings";
    public static final String NAME_VARIABLE = "name";
    public static final String PARENT_WF = "parent-wf";
    public static final String CHILD_WF = "child-wf";

    @LHWorkflow(PARENT_WF)
    public void parent(WorkflowThread wf) {
        wf.execute(GREETINGS_TASK, PARENT_WF, wf.declareStr(NAME_VARIABLE).asPublic());
    }

    @LHWorkflow(value = CHILD_WF, parent = PARENT_WF)
    public void child(WorkflowThread wf) {
        wf.execute(GREETINGS_TASK, CHILD_WF, wf.declareStr(NAME_VARIABLE).asInherited());
    }

    @LHTaskMethod(GREETINGS_TASK)
    public void greetings(String wfName, String name) {
        System.out.printf("Hello %s from %s %n", name, wfName);
    }
}
