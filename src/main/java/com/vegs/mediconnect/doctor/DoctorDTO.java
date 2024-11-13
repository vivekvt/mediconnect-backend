package com.vegs.mediconnect.doctor;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DoctorDTO {

    @Size(max = 255)
    @DoctorIdValid
    private String id;

    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;

    @Size(max = 255)
    private String experienceInYears;

    @Size(max = 10)
    private String score;

    private String about;

}
