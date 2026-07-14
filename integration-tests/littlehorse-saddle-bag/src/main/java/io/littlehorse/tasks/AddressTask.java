package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.structs.Address;

@LHTask
public class AddressTask {

    public static final String CREATE_ADDRESS_TASK = "${task.create-address.name}";

    @LHTaskMethod(value = CREATE_ADDRESS_TASK, description = "Builds an address from its parts")
    public Address createAddress(String street, int number) {
        return new Address(street, number);
    }
}
