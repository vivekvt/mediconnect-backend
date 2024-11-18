package com.vegs.mediconnect.doctor.api.model;

import com.vegs.mediconnect.schedule.api.model.ScheduleResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DoctorResponse {

    private String firstName;
    private String lastName;
    private String name;

    private String experienceYears;
    private Float score;
    private Integer reviewCount;
    private String description;
    private String photo;
    private List<ScheduleResponse> schedule;


}
