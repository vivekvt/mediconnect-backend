package com.vegs.mediconnect.schedule_time;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ScheduleTimeDTO {

    private UUID id;

    @Size(max = 255)
    private String time;

    private Boolean available;

    @NotNull
    private UUID scheduleId;

}
