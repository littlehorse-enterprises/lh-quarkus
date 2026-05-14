package io.littlehorse.saddlebag.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.saddlebag.structs.Order;
import io.littlehorse.saddlebag.structs.ShippingAddress;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class OrderTask {

    public static final String VALIDATE_ORDER = "validate-order";
    public static final String SHIP_ORDER = "ship-order";

    @LHTaskMethod(VALIDATE_ORDER)
    public boolean validateOrder(Order order) {
        return order.getQuantity() > 0 && order.getPrice() > 0;
    }

    @LHTaskMethod(SHIP_ORDER)
    public String shipOrder(Order order, ShippingAddress address) {
        return "Shipping %s to %s".formatted(order.getProductName(), address);
    }
}
