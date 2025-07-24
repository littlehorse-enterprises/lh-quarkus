package io.littlehorse.workflows;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.tasks.PrintTask;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WorkflowProducer {

    @LHWorkflow("hello-world")
    public void helloWorld(WorkflowThread wf) {
        wf.execute(PrintTask.PRINT_TASK, "Hello World!");
    }

    @LHWorkflow("test")
    public void test(WorkflowThread wf) {
        wf.execute(PrintTask.PRINT_TASK, "This is a test!");
    }
}
