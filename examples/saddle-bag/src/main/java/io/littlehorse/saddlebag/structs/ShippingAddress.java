package io.littlehorse.saddlebag.structs;

import io.littlehorse.sdk.worker.LHStructDef;

@LHStructDef("shipping-address")
public class ShippingAddress {
    private String street;
    private String city;
    private String state;
    private int zipCode;

    public ShippingAddress() {}

    public ShippingAddress(String street, String city, String state, int zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "%s, %s, %s %d".formatted(street, city, state, zipCode);
    }
}
