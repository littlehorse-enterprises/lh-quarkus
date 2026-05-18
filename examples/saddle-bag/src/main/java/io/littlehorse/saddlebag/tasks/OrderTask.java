package io.littlehorse.saddlebag.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.saddlebag.structs.Order;
import io.littlehorse.saddlebag.structs.ShippingAddress;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class OrderTask {

    public static final String VALIDATE_ORDER = "${task.validate-order.name}";
    public static final String SHIP_ORDER = "${task.ship-order.name}";

    @LHTaskMethod(
            value = VALIDATE_ORDER,
            description = "Validates that an order has positive quantity and price")
    public boolean validateOrder(Order order) {
        return order.getQuantity() > 0 && order.getPrice() > 0;
    }

    @LHTaskMethod(
            value = SHIP_ORDER,
            description = "Ships an order to the specified address and returns a confirmation")
    public String shipOrder(Order order, ShippingAddress address) {
        return "Shipping %s to %s".formatted(order.getProductName(), address);
    }
}
