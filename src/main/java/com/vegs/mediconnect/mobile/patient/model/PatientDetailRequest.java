package com.vegs.mediconnect.mobile.patient.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PatientDetailRequest {

    private String email;
    private String clinicCode;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthdate;
    private String phoneNumber;
    private String address;

}
