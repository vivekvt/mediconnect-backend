package com.vegs.mediconnect.schedule;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
public class ScheduleDTO {

    private UUID id;

    private LocalDate date;

    private Boolean available;

    @NotNull
    private UUID doctorId;

    private String doctorName;

}
