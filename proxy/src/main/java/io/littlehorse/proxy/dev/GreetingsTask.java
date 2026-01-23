package io.littlehorse.proxy.dev;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.arc.profile.IfBuildProfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IfBuildProfile("dev")
@LHTask
public class GreetingsTask {

    private static final Logger log = LoggerFactory.getLogger(GreetingsTask.class);
    public static final String GREETINGS_TASK = "greetings";
    public static final String NAME_VARIABLE = "name";
    public static final String GREETINGS_WORKFLOW = "greetings";

    @LHWorkflow(GREETINGS_WORKFLOW)
    public void greetingsWorkflow(WorkflowThread wf) {
        wf.execute(GREETINGS_TASK, wf.declareStr(NAME_VARIABLE));
    }

    @LHTaskMethod(GREETINGS_TASK)
    public void greetingsTask(String name) {
        log.info("Hello {}!", name);
    }
}
