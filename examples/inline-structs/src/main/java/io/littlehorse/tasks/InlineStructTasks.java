package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.common.proto.InlineStruct;
import io.littlehorse.sdk.common.proto.StructField;
import io.littlehorse.sdk.common.proto.VariableValue;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;
import io.littlehorse.structs.Customer;

import java.util.UUID;

@LHTask
public class InlineStructTasks {

    public static final String WORKFLOW_NAME = "inline-structs";
    public static final String CREATE_CUSTOMER_TASK = "create-customer";
    public static final String EMAIL_CUSTOMER_TASK = "email-customer";
    public static final String NAME_VAR = "name";
    public static final String EMAIL_VAR = "email";
    public static final String MESSAGE_VAR = "message";
    public static final String CUSTOMER_VAR = "customer";

    @LHWorkflow(WORKFLOW_NAME)
    public void workflow(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr(NAME_VAR).required();
        WfRunVariable email = wf.declareStr(EMAIL_VAR).required();
        WfRunVariable message = wf.declareStr(MESSAGE_VAR).required();
        WfRunVariable customer = wf.declareStruct(CUSTOMER_VAR, Customer.class);

        customer.assign(wf.execute(CREATE_CUSTOMER_TASK, name, email));
        wf.execute(EMAIL_CUSTOMER_TASK, customer, message);
    }

    @LHTaskMethod(
            value = CREATE_CUSTOMER_TASK,
            description = "Creates and returns an inline Customer struct.")
    @LHType(structDefName = "${customer.struct.name}")
    public InlineStruct createCustomer(String name, String email) {
        return InlineStruct.newBuilder()
                .putFields(
                        "id",
                        StructField.newBuilder()
                                .setValue(VariableValue.newBuilder()
                                        .setStr(UUID.randomUUID().toString()))
                                .build())
                .putFields(
                        "name",
                        StructField.newBuilder()
                                .setValue(VariableValue.newBuilder().setStr(name))
                                .build())
                .putFields(
                        "email",
                        StructField.newBuilder()
                                .setValue(VariableValue.newBuilder().setStr(email))
                                .build())
                .build();
    }

    @LHTaskMethod(
            value = EMAIL_CUSTOMER_TASK,
            description = "Receives an inline Customer struct and sends it a message.")
    public String emailCustomer(
            @LHType(structDefName = "${customer.struct.name}") InlineStruct customer,
            String message) {
        String name = customer.getFieldsOrThrow("name").getValue().getStr();
        String email = customer.getFieldsOrThrow("email").getValue().getStr();
        System.out.printf("Sending '%s' to %s <%s>%n", message, name, email);
        return "sent";
    }
}
