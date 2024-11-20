package com.vegs.mediconnect.backoffice.appointment;

import com.vegs.mediconnect.backoffice.util.NotFoundException;
import com.vegs.mediconnect.backoffice.util.ReferencedWarning;
import com.vegs.mediconnect.datasource.appointment.Appointment;
import com.vegs.mediconnect.datasource.appointment.AppointmentRepository;
import com.vegs.mediconnect.datasource.doctor.DoctorRepository;
import com.vegs.mediconnect.datasource.patient.PatientRepository;
import com.vegs.mediconnect.datasource.schedule.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;

    public AppointmentService(final AppointmentRepository appointmentRepository,
            final PatientRepository patientRepository, final DoctorRepository doctorRepository,
            final ScheduleRepository scheduleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public List<AppointmentDTO> findAll() {
        final List<Appointment> appointments = appointmentRepository.findAll(Sort.by("id"));
        return appointments.stream()
                .map(appointment -> mapToDTO(appointment, new AppointmentDTO()))
                .toList();
    }

    @Transactional
    public AppointmentDTO get(final UUID id) {
        return appointmentRepository.findById(id)
                .map(appointment -> mapToDTO(appointment, new AppointmentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final AppointmentDTO appointmentDTO) {
        final Appointment appointment = new Appointment();
        mapToEntity(appointmentDTO, appointment);
        return appointmentRepository.save(appointment).getId();
    }

    public void update(final UUID id, final AppointmentDTO appointmentDTO) {
        final Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(appointmentDTO, appointment);
        appointmentRepository.save(appointment);
    }

    public void delete(final UUID id) {
        appointmentRepository.deleteById(id);
    }

    private AppointmentDTO mapToDTO(final Appointment appointment,
            final AppointmentDTO appointmentDTO) {
        appointmentDTO.setId(appointment.getId());
        appointmentDTO.setStatus(appointment.getStatus());
        appointmentDTO.setAvailable(appointment.getAvailable());
        appointmentDTO.setPatientId(appointment.getPatient().getId());
        appointmentDTO.setDoctorId(appointment.getDoctor().getId());
        appointmentDTO.setScheduleId(appointment.getSchedule().getId());
        return appointmentDTO;
    }

    private Appointment mapToEntity(final AppointmentDTO appointmentDTO,
            final Appointment appointment) {
        appointment.setStatus(appointmentDTO.getStatus());
        appointment.setAvailable(appointmentDTO.getAvailable());
        appointment.setPatient(patientRepository.getReferenceById(appointmentDTO.getPatientId()));
        appointment.setDoctor(doctorRepository.getReferenceById(appointmentDTO.getDoctorId()));
        appointment.setSchedule(scheduleRepository.getReferenceById(appointmentDTO.getScheduleId()));
        return appointment;
    }

    public ReferencedWarning getReferencedWarning(final UUID id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        return null;
    }

}
