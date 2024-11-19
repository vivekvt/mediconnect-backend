package com.vegs.mediconnect.mobile.doctor.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DoctorSimpleResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String name;
    private String speciality;
    private String photo;

}
