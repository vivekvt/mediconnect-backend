package com.vegs.mediconnect.appointment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class AppointmentDTO {

    private UUID id;

    @Size(max = 255)
    private String status;

    @NotNull
    private Boolean available;

    @NotNull
    private UUID patientId;

    @NotNull
    private UUID doctorId;

    @NotNull
    private UUID scheduleId;

}
