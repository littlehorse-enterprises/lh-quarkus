package io.littlehorse.workflows;

import io.littlehorse.quarkus.workflow.LHExponentialBackoffRetry;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class InsideBeanWorkflow {

    @ConfigProperty(name = "task.print.name")
    String taskPrintName;

    @LHWorkflow(
            value = "workflow-in-a-bean",
            defaultTaskExponentialBackoffRetry =
                    @LHExponentialBackoffRetry(
                            baseIntervalMs = "${retry.base.interval.ms}",
                            maxDelayMs = "${retry.max.delay.ms}",
                            multiplier = "${retry.multiplier}"))
    public void myWorkflow(WorkflowThread wf) {
        wf.execute(taskPrintName, "Hello World");
    }
}
