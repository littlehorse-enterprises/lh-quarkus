package io.littlehorse.workflows;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.workers.PrintTask;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WorkflowProducer {

    @LHWorkflow("hello-world")
    public void helloWorld(WorkflowThread wf) {
        wf.execute(PrintTask.TASK_PRINT, "Hello World!");
    }

    @LHWorkflow("test")
    public void test(WorkflowThread wf) {
        wf.execute(PrintTask.TASK_PRINT, "This is a test!");
    }
}
