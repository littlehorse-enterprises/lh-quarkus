package io.littlehorse.saddlebag.structs;

import io.littlehorse.sdk.worker.LHStructDef;

@LHStructDef("order")
public class Order {
    private String productName;
    private int quantity;
    private double price;
    private ShippingAddress shippingAddress;

    public Order() {}

    public Order(String productName, int quantity, double price, ShippingAddress shippingAddress) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.shippingAddress = shippingAddress;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Override
    public String toString() {
        return "%s x%d ($%.2f) -> %s".formatted(productName, quantity, price, shippingAddress);
    }
}
