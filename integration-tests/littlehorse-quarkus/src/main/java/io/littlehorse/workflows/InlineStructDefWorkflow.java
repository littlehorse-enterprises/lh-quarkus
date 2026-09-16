package io.littlehorse.workflows;

import static io.littlehorse.tasks.InlineStructDefTask.EMAIL_INLINE_STRUCT_DEF_CUSTOMER_TASK;
import static io.littlehorse.tasks.InlineStructDefTask.NORMALIZE_INLINE_STRUCT_DEF_CUSTOMER_TASK;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.structs.InlineStructDefCustomer;

@LHWorkflow(InlineStructDefWorkflow.INLINE_STRUCT_DEF_WORKFLOW)
public class InlineStructDefWorkflow implements LHWorkflowDefinition {

    public static final String INLINE_STRUCT_DEF_WORKFLOW = "inline-struct-def";
    public static final String MESSAGE_VARIABLE = "message";
    public static final String CUSTOMER_VARIABLE = "inline-struct-def-customer";
    public static final String NORMALIZED_CUSTOMER_VARIABLE =
            "normalized-inline-struct-def-customer";
    public static final String DELIVERY_VARIABLE = "inline-struct-def-delivery";

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable message = wf.declareStr(MESSAGE_VARIABLE).required();
        WfRunVariable customer = wf.declareInlineStruct(
                        CUSTOMER_VARIABLE, InlineStructDefCustomer.class)
                .required();
        WfRunVariable normalizedCustomer =
                wf.declareInlineStruct(NORMALIZED_CUSTOMER_VARIABLE, InlineStructDefCustomer.class);
        WfRunVariable delivery = wf.declareStr(DELIVERY_VARIABLE);

        normalizedCustomer.assign(wf.execute(NORMALIZE_INLINE_STRUCT_DEF_CUSTOMER_TASK, customer));
        delivery.assign(
                wf.execute(EMAIL_INLINE_STRUCT_DEF_CUSTOMER_TASK, normalizedCustomer, message));
    }
}
