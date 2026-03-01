package io.littlehorse.structs;

import io.littlehorse.sdk.worker.LHStructDef;

import java.util.Date;

@LHStructDef("parking-ticket-report")
public class ParkingTicketReport {
    private String vehicleMake;
    private String vehicleModel;
    private String licensePlateNumber;
    private Date createdAt;

    public ParkingTicketReport() {}

    public ParkingTicketReport(
            String vehicleMake, String vehicleModel, String licensePlateNumber, Date createdAt) {
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.licensePlateNumber = licensePlateNumber;
        this.createdAt = createdAt;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s, Plate Number: %s, issued at %s",
                vehicleMake, vehicleModel, licensePlateNumber, createdAt.toString());
    }
}
