package com.vegs.mediconnect.schedule_time;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleDateTimeDTO {

    private LocalTime time;
    private UUID scheduleId;

}
