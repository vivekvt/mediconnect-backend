package com.vegs.mediconnect.mobile.appointment.model;

public enum AppointmentStatus {

    UPCOMING("UPCOMING"),
    CANCELED("CANCELED"),
    COMPLETED("COMPLETED");


    AppointmentStatus(String status) {
        this.status = status;
    }

    private final String status;

    public String getStatus() {
        return status;
    }
}
