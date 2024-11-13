package com.vegs.mediconnect.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ScheduleDTO {

    private UUID id;

    private LocalDate date;

    private Boolean available;

    @NotNull
    @Size(max = 255)
    @JsonProperty("dSscheduleId")
    private String dSscheduleId;

    @NotNull
    @ScheduleSAscheduleIdUnique
    @JsonProperty("sAscheduleId")
    private UUID sAscheduleId;

}
