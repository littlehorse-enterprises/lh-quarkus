package io.littlehorse.workflows;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.ThreadFunc;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

@LHWorkflow("greetings")
public class GreetingsWorkflow implements ThreadFunc {

    @Override
    public void threadFunction(WorkflowThread thread) {}
}
