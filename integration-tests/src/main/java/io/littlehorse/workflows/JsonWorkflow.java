package io.littlehorse.workflows;

import static io.littlehorse.proxy.dev.JsonTask.RETURN_JSON_ARRAY;
import static io.littlehorse.proxy.dev.JsonTask.RETURN_JSON_LIST;
import static io.littlehorse.proxy.dev.JsonTask.RETURN_JSON_OBJECT;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

@LHWorkflow(JsonWorkflow.JSON_WORKFLOW)
public class JsonWorkflow implements LHWorkflowDefinition {
    public static final String JSON_WORKFLOW = "json";
    public static final String CHILDREN_VARIABLE = "children";
    public static final String FAMILY_VARIABLE = "family";
    public static final String CHARACTER_VARIABLE = "character";

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable character = wf.declareJsonObj(CHARACTER_VARIABLE);
        WfRunVariable children = wf.declareJsonArr(CHILDREN_VARIABLE);
        WfRunVariable family = wf.declareJsonArr(FAMILY_VARIABLE);

        character.assign(wf.execute(RETURN_JSON_OBJECT));
        children.assign(wf.execute(RETURN_JSON_ARRAY));
        family.assign(wf.execute(RETURN_JSON_LIST));
    }
}
