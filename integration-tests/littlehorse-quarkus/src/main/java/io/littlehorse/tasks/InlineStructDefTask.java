package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;
import io.littlehorse.structs.InlineStructDefAddress;
import io.littlehorse.structs.InlineStructDefCustomer;

import java.util.Map;

@LHTask
public class InlineStructDefTask {

    public static final String NORMALIZE_INLINE_STRUCT_DEF_CUSTOMER_TASK =
            "normalize-inline-struct-def-customer";
    public static final String EMAIL_INLINE_STRUCT_DEF_CUSTOMER_TASK =
            "email-inline-struct-def-customer";

    @LHTaskMethod(NORMALIZE_INLINE_STRUCT_DEF_CUSTOMER_TASK)
    @LHType(isInlineStruct = true)
    public InlineStructDefCustomer normalizeCustomer(
            @LHType(isInlineStruct = true) InlineStructDefCustomer customer) {
        customer.getAddress().setCity(customer.getAddress().getCity().trim());
        for (InlineStructDefAddress address : customer.getPreviousAddresses()) {
            address.setCity(address.getCity().trim());
        }
        for (Map.Entry<String, InlineStructDefAddress> entry :
                customer.getAddressesByLabel().entrySet()) {
            entry.getValue().setCity(entry.getValue().getCity().trim());
        }
        return customer;
    }

    @LHTaskMethod(EMAIL_INLINE_STRUCT_DEF_CUSTOMER_TASK)
    public String emailCustomer(
            @LHType(isInlineStruct = true) InlineStructDefCustomer customer, String message) {
        return "%s <%s> in %s: %s"
                .formatted(
                        customer.getName(),
                        customer.getEmail(),
                        customer.getAddress().getCity(),
                        message);
    }
}
