package io.littlehorse.structs;

import io.littlehorse.sdk.worker.LHStructDef;

@LHStructDef("${struct.address.name}")
public class Address {

    private String street;
    private int number;

    public Address() {}

    public Address(String street, int number) {
        this.street = street;
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
