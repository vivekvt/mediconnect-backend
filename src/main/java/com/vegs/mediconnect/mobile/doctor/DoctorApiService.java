package com.vegs.mediconnect.mobile.doctor;

import com.vegs.mediconnect.datasource.doctor.Doctor;
import com.vegs.mediconnect.datasource.doctor.DoctorRepository;
import com.vegs.mediconnect.mobile.doctor.model.DoctorResponse;
import com.vegs.mediconnect.mobile.doctor.model.DoctorSimpleResponse;
import com.vegs.mediconnect.mobile.schedule.model.ScheduleResponse;
import com.vegs.mediconnect.datasource.schedule.ScheduleTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class DoctorApiService {

    private final DoctorRepository doctorRepository;
    @Value("${vegs.photo-baseurl}")
    private String photoBaseulr;

    public List<DoctorSimpleResponse> getDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> DoctorSimpleResponse
                        .builder()
                        .id(doctor.getId().toString())
                        .photo(getPhotoUrl(doctor))
                        .specialty(doctor.getSpecialty())
                        .name(doctor.getFullName())
                        .firstName(doctor.getFirstName())
                        .lastName(doctor.getLastName())
                        .build()
                ).toList();
    }

    private String getPhotoUrl(Doctor doctor) {
        return format("%s/api/mobile/doctors/photo/%s", photoBaseulr, doctor.getId().toString());
    }

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
                .photo(getPhotoUrl(doctor))
                // Calculate score
                .score(4.9f)
                .description(doctor.getAbout())
                .experienceYears(doctor.getExperienceInYears())
                .reviewCount(randomReviewCount)
                .schedule(schedule)
                .build();
    }

    public Doctor getDoctor(UUID doctorId) {
        var optDoctor = doctorRepository.findById(doctorId);
        if (optDoctor.isEmpty()) {
            throw new DoctorNotFoundException();
        }
        return optDoctor.get();
    }

}
