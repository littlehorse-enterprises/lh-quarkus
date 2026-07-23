package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.common.proto.InlineStruct;
import io.littlehorse.sdk.common.proto.StructField;
import io.littlehorse.sdk.common.proto.VariableValue;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;

@LHTask
public class InlineStructTask {

    public static final String CREATE_INLINE_CUSTOMER_TASK = "create-inline-customer";
    public static final String EMAIL_INLINE_CUSTOMER_TASK = "email-inline-customer";
    private static final String INLINE_CUSTOMER_STRUCT = "${struct.inline-customer.name}";

    @LHTaskMethod(CREATE_INLINE_CUSTOMER_TASK)
    @LHType(structDefName = INLINE_CUSTOMER_STRUCT)
    public InlineStruct createCustomer(String id, String name, String email) {
        return InlineStruct.newBuilder()
                .putFields("id", stringField(id))
                .putFields("name", stringField(name))
                .putFields("email", stringField(email))
                .build();
    }

    @LHTaskMethod(EMAIL_INLINE_CUSTOMER_TASK)
    public String emailCustomer(
            @LHType(structDefName = INLINE_CUSTOMER_STRUCT) InlineStruct customer, String message) {
        String name = customer.getFieldsOrThrow("name").getValue().getStr();
        String email = customer.getFieldsOrThrow("email").getValue().getStr();
        return "%s <%s>: %s".formatted(name, email, message);
    }

    private static StructField stringField(String value) {
        return StructField.newBuilder()
                .setValue(VariableValue.newBuilder().setStr(value))
                .build();
    }
}
