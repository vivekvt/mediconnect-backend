package com.vegs.mediconnect.mobile.doctor;

import com.vegs.mediconnect.datasource.doctor.Doctor;
import com.vegs.mediconnect.datasource.doctor.DoctorRepository;
import com.vegs.mediconnect.datasource.schedule.Schedule;
import com.vegs.mediconnect.datasource.schedule.ScheduleTime;
import com.vegs.mediconnect.mobile.doctor.model.DoctorResponse;
import com.vegs.mediconnect.mobile.doctor.model.DoctorSimpleResponse;
import com.vegs.mediconnect.mobile.schedule.model.ScheduleResponse;
import com.vegs.mediconnect.mobile.schedule.model.ScheduleTimeResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                .map(this::mapToDoctorSimpleResponse)
                .toList();
    }

    public Doctor getDoctor(UUID doctorId) {
        var optDoctor = doctorRepository.findById(doctorId);
        if (optDoctor.isEmpty()) {
            throw new DoctorNotFoundException();
        }
        return optDoctor.get();
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
        var scheduleResponses = new ArrayList<ScheduleResponse>();
        doctor
                .getSchedules()
                .stream()
                .filter(DoctorApiService::isTodayOrLate)
                .sorted(Comparator.comparing(Schedule::getDate))
                .forEach(schedule -> addIfApplicable(schedule, dateFormat, timeFormat, scheduleResponses));

        var randomReview = createRandomReview();

        return DoctorResponse
                .builder()
                .name(doctor.getFullName())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .photo(getPhotoUrl(doctor))
                .description(doctor.getAbout())
                .experienceYears(doctor.getExperienceInYears())
                .score(randomReview.score())
                .reviewCount(randomReview.count())
                .schedule(scheduleResponses)
                .build();
    }

    private void addIfApplicable(Schedule schedule,
                                 DateTimeFormatter dateFormat, DateTimeFormatter timeFormat,
                                 List<ScheduleResponse> scheduleResponses) {
        var times = getTimes(schedule, timeFormat);
        if (!times.isEmpty()) {
            scheduleResponses.add(ScheduleResponse
                    .builder()
                    .date(schedule.getDate().format(dateFormat))
                    .times(times)
                    .build());
        }
    }

    private static boolean isTodayOrLate(Schedule schedule) {
        return !LocalDate.now().isAfter(schedule.getDate());
    }

    public DoctorSimpleResponse mapToDoctorSimpleResponse(Doctor doctor) {
        var randomReview = createRandomReview();
        return DoctorSimpleResponse
                .builder()
                .id(doctor.getId())
                .photo(getPhotoUrl(doctor))
                .specialty(doctor.getSpecialty())
                .name(doctor.getFullName())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .score(randomReview.score())
                .reviewCount(randomReview.count())
                .build();
    }

    private List<ScheduleTimeResponse> getTimes(Schedule schedule, DateTimeFormatter timeFormat) {
        return schedule
                .getScheduleTimes()
                .stream()
                .filter(ScheduleTime::getAvailable)
                .filter(this::isAtLeastSixHoursAhead)
                .sorted(Comparator.comparing(ScheduleTime::getTime))
                .map(scheduleTime -> ScheduleTimeResponse
                        .builder()
                        .id(scheduleTime.getId())
                        .time(scheduleTime.getTime().format(timeFormat))
                        .build())
                .toList();
    }

    private boolean isAtLeastSixHoursAhead(ScheduleTime scheduleTime) {
        return scheduleTime
                .getDateTime()
                .isAfter(LocalDateTime
                        .now()
                        .plusHours(6));
    }

    private String getPhotoUrl(Doctor doctor) {
        return format("%s/api/mobile/doctors/photo/%s", photoBaseulr, doctor.getId().toString());
    }

    private Review createRandomReview() {
        // Fake Review
        Random random = new Random();
        int randomReviewCount = 30 + random.nextInt(71); // Range [30, 100]
        float randomScore = 1.0f + random.nextFloat() * (5.0f - 1.0f); // Range [1.0, 5.0]
        randomScore = Math.round(randomScore * 10) / 10.0f;

        return new Review(randomReviewCount, randomScore);
    }

    record Review(Integer count, Float score) {}

}
