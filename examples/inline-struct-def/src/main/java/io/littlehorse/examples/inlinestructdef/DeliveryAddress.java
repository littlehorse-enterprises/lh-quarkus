package io.littlehorse.examples.inlinestructdef;

public class DeliveryAddress {

    private String street;
    private String city;

    public DeliveryAddress() {}

    public DeliveryAddress(String street, String city) {
        this.street = street;
        this.city = city;
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
}
