package io.littlehorse.workflows;

import static io.littlehorse.tasks.InlineStructTask.CREATE_INLINE_CUSTOMER_TASK;
import static io.littlehorse.tasks.InlineStructTask.EMAIL_INLINE_CUSTOMER_TASK;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.structs.InlineCustomer;

@LHWorkflow(InlineStructWorkflow.INLINE_STRUCT_WORKFLOW)
public class InlineStructWorkflow implements LHWorkflowDefinition {

    public static final String INLINE_STRUCT_WORKFLOW = "inline-structs";
    public static final String ID_VARIABLE = "id";
    public static final String NAME_VARIABLE = "name";
    public static final String EMAIL_VARIABLE = "email";
    public static final String MESSAGE_VARIABLE = "message";
    public static final String CUSTOMER_VARIABLE = "customer";
    public static final String DELIVERY_VARIABLE = "delivery";

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable id = wf.declareStr(ID_VARIABLE).required();
        WfRunVariable name = wf.declareStr(NAME_VARIABLE).required();
        WfRunVariable email = wf.declareStr(EMAIL_VARIABLE).required();
        WfRunVariable message = wf.declareStr(MESSAGE_VARIABLE).required();
        WfRunVariable customer = wf.declareStruct(CUSTOMER_VARIABLE, InlineCustomer.class);
        WfRunVariable delivery = wf.declareStr(DELIVERY_VARIABLE);

        customer.assign(wf.execute(CREATE_INLINE_CUSTOMER_TASK, id, name, email));
        delivery.assign(wf.execute(EMAIL_INLINE_CUSTOMER_TASK, customer, message));
    }
}
