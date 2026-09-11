package io.littlehorse.structs;

import java.util.Map;

public class InlineStructDefCustomer {

    private String name;
    private String email;
    private InlineStructDefAddress address;
    private InlineStructDefAddress[] previousAddresses;
    private Map<String, InlineStructDefAddress> addressesByLabel;

    public InlineStructDefCustomer() {}

    public InlineStructDefCustomer(
            String name,
            String email,
            InlineStructDefAddress address,
            InlineStructDefAddress[] previousAddresses,
            Map<String, InlineStructDefAddress> addressesByLabel) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.previousAddresses = previousAddresses;
        this.addressesByLabel = addressesByLabel;
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

    public InlineStructDefAddress getAddress() {
        return address;
    }

    public void setAddress(InlineStructDefAddress address) {
        this.address = address;
    }

    public InlineStructDefAddress[] getPreviousAddresses() {
        return previousAddresses;
    }

    public void setPreviousAddresses(InlineStructDefAddress[] previousAddresses) {
        this.previousAddresses = previousAddresses;
    }

    public Map<String, InlineStructDefAddress> getAddressesByLabel() {
        return addressesByLabel;
    }

    public void setAddressesByLabel(Map<String, InlineStructDefAddress> addressesByLabel) {
        this.addressesByLabel = addressesByLabel;
    }
}
