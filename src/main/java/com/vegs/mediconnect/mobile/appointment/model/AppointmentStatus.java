package com.vegs.mediconnect.mobile.appointment.model;

public enum AppointmentStatus {

    CREATED("CREATED"),
    CANCELED("CANCELED");


    AppointmentStatus(String status) {
        this.status = status;
    }

    private final String status;

    public String getStatus() {
        return status;
    }
}
