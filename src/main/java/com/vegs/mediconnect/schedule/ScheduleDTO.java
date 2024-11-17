package com.vegs.mediconnect.schedule;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
public class ScheduleDTO {

    private UUID id;

    @NotNull
    private LocalDate date;

    @NotNull
    private UUID doctorId;

    private String doctorName;

    @ScheduleDoctorDateUnique
    public DoctorDateDTO getDoctorDate() {
        return new DoctorDateDTO(date, doctorId);
    }
}
