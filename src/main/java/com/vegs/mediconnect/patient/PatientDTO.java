package com.vegs.mediconnect.patient;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class PatientDTO {

    private UUID id;

    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;

}
