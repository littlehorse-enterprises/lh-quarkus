package io.littlehorse.examples.inlinestructdef;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;

@LHTask
public class InlineStructDefTasks {

    public static final String WORKFLOW_NAME = "inline-struct-def";
    public static final String NORMALIZE_CUSTOMER_TASK = "normalize-customer";
    public static final String EMAIL_CUSTOMER_TASK = "email-customer";
    public static final String MESSAGE_VAR = "message";
    public static final String CUSTOMER_VAR = "customer";
    public static final String NORMALIZED_CUSTOMER_VAR = "normalized-customer";

    @LHWorkflow(WORKFLOW_NAME)
    public void workflow(WorkflowThread wf) {
        WfRunVariable message = wf.declareStr(MESSAGE_VAR).required();
        WfRunVariable customer =
                wf.declareInlineStruct(CUSTOMER_VAR, Customer.class).required();
        WfRunVariable normalizedCustomer =
                wf.declareInlineStruct(NORMALIZED_CUSTOMER_VAR, Customer.class);

        normalizedCustomer.assign(wf.execute(NORMALIZE_CUSTOMER_TASK, customer));
        wf.execute(EMAIL_CUSTOMER_TASK, normalizedCustomer, message);
    }

    @LHTaskMethod(value = NORMALIZE_CUSTOMER_TASK, description = "Normalizes a customer address.")
    @LHType(isInlineStruct = true)
    public Customer normalizeCustomer(@LHType(isInlineStruct = true) Customer customer) {
        customer.getAddress().setStreet(customer.getAddress().getStreet().trim());
        customer.getAddress().setCity(customer.getAddress().getCity().trim());
        return customer;
    }

    @LHTaskMethod(
            value = EMAIL_CUSTOMER_TASK,
            description = "Receives an anonymous Customer struct and sends it a message.")
    public String emailCustomer(@LHType(isInlineStruct = true) Customer customer, String message) {
        System.out.printf(
                "Sending '%s' to %s <%s> at %s, %s%n",
                message,
                customer.getName(),
                customer.getEmail(),
                customer.getAddress().getStreet(),
                customer.getAddress().getCity());
        return "sent";
    }
}
