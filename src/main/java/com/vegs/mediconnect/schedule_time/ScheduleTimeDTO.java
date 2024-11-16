package com.vegs.mediconnect.schedule_time;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;


@Getter
@Setter
public class ScheduleTimeDTO {

    private UUID id;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm a")
    private LocalTime time;

    @NotNull
    private Boolean available;

    @NotNull
    private UUID scheduleId;

    private LocalDate scheduleDate;

    private String doctorName;

}
