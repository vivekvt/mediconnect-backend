package com.vegs.mediconnect.patient;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PatientDTO {

    @Size(max = 255)
    @PatientIdValid
    private String id;

    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;

}
