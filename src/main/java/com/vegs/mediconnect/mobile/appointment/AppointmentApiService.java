package com.vegs.mediconnect.mobile.appointment;

import com.vegs.mediconnect.datasource.appointment.Appointment;
import com.vegs.mediconnect.datasource.appointment.AppointmentRepository;
import com.vegs.mediconnect.datasource.doctor.Doctor;
import com.vegs.mediconnect.datasource.patient.Patient;
import com.vegs.mediconnect.datasource.patient.PatientRepository;
import com.vegs.mediconnect.datasource.schedule.ScheduleTime;
import com.vegs.mediconnect.datasource.schedule.ScheduleTimeRepository;
import com.vegs.mediconnect.mobile.appointment.model.AppointmentRequest;
import com.vegs.mediconnect.mobile.appointment.model.AppointmentResponse;
import com.vegs.mediconnect.mobile.appointment.model.AppointmentStatus;
import com.vegs.mediconnect.mobile.doctor.DoctorApiService;
import com.vegs.mediconnect.mobile.patient.PatientNotFoundException;
import com.vegs.mediconnect.mobile.schedule.ScheduleTimeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentApiService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ScheduleTimeRepository scheduleTimeRepository;
    private final DoctorApiService doctorApiService;

    @Transactional
    public AppointmentResponse create(AppointmentRequest appointmentRequest) {
        var appointment = mapToEntity(appointmentRequest.getScheduleTimeId(), appointmentRequest.getPatientEmail());
        var scheduleTime = appointment.getScheduleTime();
        // Make Schedule Time unavailable
        scheduleTime.setAvailable(false);
        scheduleTimeRepository.save(scheduleTime);
        // Create new appointment
        var createdAppointment = appointmentRepository.save(appointment);
        return mapToAppointmentResponse(createdAppointment);
    }

    private Appointment mapToEntity(UUID scheduleTimeId, String patientEmail) {
        // Get ScheduleTime Entity
        var scheduleTime = scheduleTimeRepository.findById(scheduleTimeId)
                .orElseThrow(ScheduleTimeNotFoundException::new);

        // Get Patient Entity
        var patient = patientRepository.findByEmail(patientEmail)
                .orElseThrow(PatientNotFoundException::new);

        // Get Doctor Entity
        var doctor = scheduleTime.getSchedule().getDoctor();

        // Map to Appointment Entity
        return mapToEntity(scheduleTime, patient, doctor);
    }

    private AppointmentResponse mapToAppointmentResponse(Appointment appointment) {
        // Build AppointmentResponse
        var scheduleTime = appointment.getScheduleTime();
        var doctor = scheduleTime.getSchedule().getDoctor();
        var schedule = scheduleTime.getSchedule();
        var dataFormat = DateTimeFormatter.ofPattern("EEE, d MMM");
        var timeFormat = DateTimeFormatter.ofPattern("h a");
        return AppointmentResponse
                .builder()
                .date(schedule.getDate().format(dataFormat))
                .time(scheduleTime.getTime().format(timeFormat))
                .status(appointment.getStatus())
                .doctor(doctorApiService.mapToDoctorSimpleResponse(doctor))
                .build();
    }

    private Appointment mapToEntity(ScheduleTime scheduleTime, Patient patient, Doctor doctor) {
        var appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CREATED.getStatus());
        appointment.setCanceled(false);
        appointment.setScheduleTime(scheduleTime);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        return appointment;
    }

    @Transactional
    public List<AppointmentResponse> getAppointments(String email) {
        var optPatient = patientRepository.findByEmail(email);
        if (optPatient.isEmpty()) {
            return List.of();
        }
        var patient = optPatient.get();

        return appointmentRepository.findAllByPatient(patient)
                .stream()
                .map(this::mapToAppointmentResponse)
                .toList();
    }

}
