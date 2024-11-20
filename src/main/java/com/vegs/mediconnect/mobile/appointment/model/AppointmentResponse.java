package com.vegs.mediconnect.mobile.appointment.model;

import com.vegs.mediconnect.mobile.doctor.model.DoctorSimpleResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AppointmentResponse {

    private String status;
    private String date;
    private String time;
    private DoctorSimpleResponse doctor;

}
