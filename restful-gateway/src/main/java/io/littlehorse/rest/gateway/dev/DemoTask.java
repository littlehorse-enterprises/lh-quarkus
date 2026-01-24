package io.littlehorse.rest.gateway.dev;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.arc.profile.IfBuildProfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IfBuildProfile("dev")
@LHTask
public class DemoTask {

    private static final Logger log = LoggerFactory.getLogger(DemoTask.class);
    public static final String RESTFUL_GATEWAY_DEMO_TASK = "restful-gateway-demo-task";
    public static final String RESTFUL_GATEWAY_DEMO_WORKFLOW = "restful-gateway-demo-workflow";

    @LHWorkflow(RESTFUL_GATEWAY_DEMO_WORKFLOW)
    public void demoWorkflow(WorkflowThread wf) {
        wf.execute(RESTFUL_GATEWAY_DEMO_TASK, wf.declareStr("name"));
    }

    @LHTaskMethod(RESTFUL_GATEWAY_DEMO_TASK)
    public void demoTask(String name) {
        log.info("Hello {}!", name);
    }
}
