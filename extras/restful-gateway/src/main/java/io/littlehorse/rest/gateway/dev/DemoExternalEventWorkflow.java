package io.littlehorse.rest.gateway.dev;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.arc.profile.IfBuildProfile;

@IfBuildProfile("dev")
@LHWorkflow(DemoExternalEventWorkflow.RESTFUL_GATEWAY_EXTERNAL_EVENT_DEMO_WORKFLOW)
public class DemoExternalEventWorkflow implements LHWorkflowDefinition {
    public static final String RESTFUL_GATEWAY_EXTERNAL_EVENT_DEMO_WORKFLOW =
            "restful-gateway-external-event-demo-workflow";
    public static final String RESTFUL_GATEWAY_EXTERNAL_EVENT_DEMO =
            "restful-gateway-external-event-demo";

    @Override
    public void define(WorkflowThread wf) {
        wf.waitForEvent(RESTFUL_GATEWAY_EXTERNAL_EVENT_DEMO).registeredAs(null);
    }
}
