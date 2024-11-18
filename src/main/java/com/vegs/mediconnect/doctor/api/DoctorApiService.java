package com.vegs.mediconnect.doctor.api;

import com.vegs.mediconnect.doctor.DoctorRepository;
import com.vegs.mediconnect.doctor.api.model.DoctorResponse;
import com.vegs.mediconnect.schedule.api.model.ScheduleResponse;
import com.vegs.mediconnect.schedule_time.ScheduleTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorApiService {

    private final DoctorRepository doctorRepository;

    @Transactional
    public DoctorResponse getDoctorDetails(UUID doctorId) {
        var optDoctor = doctorRepository.findById(doctorId);
        if (optDoctor.isEmpty()) {
            throw new DoctorNotFoundException();
        }

        var doctor = optDoctor.get();
        var dateFormat = DateTimeFormatter.ofPattern("EEE, d MMM");
        var timeFormat = DateTimeFormatter.ofPattern("h a");
        var schedule = new ArrayList<ScheduleResponse>();
        doctor.getSchedules().forEach(s -> schedule.add(ScheduleResponse
                .builder()
                        .date(s.getDate().format(dateFormat))
                        .times(s.getScheduleTimes()
                                .stream()
                                .map(ScheduleTime::getTime)
                                .map(time -> time.format(timeFormat))
                                .toList()
                        )
                .build()));
        Random random = new Random();
        int randomReviewCount = 30 + random.nextInt(71);
        return DoctorResponse
                .builder()
                .name(doctor.getFullName())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .score(Float.parseFloat(doctor.getScore()))
                .description(doctor.getAbout())
                .experienceYears(doctor.getExperienceInYears())
                .reviewCount(randomReviewCount)
                .schedule(schedule)
                .build();
    }

}
