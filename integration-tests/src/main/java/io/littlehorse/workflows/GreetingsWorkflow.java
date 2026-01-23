package io.littlehorse.workflows;

import static io.littlehorse.tasks.GreetingsTask.GREETINGS_TASK;

import io.littlehorse.quarkus.workflow.LHExponentialBackoffRetry;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.TaskNodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@LHWorkflow(
        value = GreetingsWorkflow.GREETINGS_WORKFLOW,
        defaultTaskExponentialBackoffRetry =
                @LHExponentialBackoffRetry(
                        baseIntervalMs = "${retry.base.interval.ms}",
                        maxDelayMs = "${retry.max.delay.ms}",
                        multiplier = "${retry.multiplier}"))
public class GreetingsWorkflow implements LHWorkflowDefinition {

    public static final String NAME_VARIABLE = "name";
    public static final String GREETINGS_WORKFLOW = "greetings";

    @ConfigProperty(name = "task.print.name")
    String taskPrintName;

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr(NAME_VARIABLE);
        TaskNodeOutput message = wf.execute(GREETINGS_TASK, name);
        wf.execute(taskPrintName, message);
    }
}
