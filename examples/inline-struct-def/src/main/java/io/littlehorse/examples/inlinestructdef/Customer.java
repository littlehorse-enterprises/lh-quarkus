package io.littlehorse.examples.inlinestructdef;

public class Customer {

    private String name;
    private String email;
    private DeliveryAddress address;

    public Customer() {}

    public Customer(String name, String email, DeliveryAddress address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DeliveryAddress getAddress() {
        return address;
    }

    public void setAddress(DeliveryAddress address) {
        this.address = address;
    }
}
