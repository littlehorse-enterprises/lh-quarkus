package io.littlehorse.structs;

import io.littlehorse.sdk.worker.LHStructDef;

@LHStructDef("address")
public class Address {
    private int houseNumber;
    private String street;
    private String city;
    private String planet;
    private int zipCode;

    public Address() {}

    public Address(int houseNumber, String street, String city, String planet, int zipCode) {
        this.houseNumber = houseNumber;
        this.street = street;
        this.city = city;
        this.planet = planet;
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "%s %s, %s, %s %d".formatted(houseNumber, street, city, planet, zipCode);
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
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

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
}
