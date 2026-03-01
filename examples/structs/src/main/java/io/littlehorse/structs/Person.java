package io.littlehorse.structs;

import io.littlehorse.sdk.worker.LHStructDef;

@LHStructDef("person")
public class Person {
    private String firstName;
    private String lastName;
    private Address homeAddress;

    public Person() {}

    public Person(String firstName, String lastName, Address homeAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.homeAddress = homeAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Override
    public String toString() {
        return String.format("%s %s", firstName, lastName);
    }
}
