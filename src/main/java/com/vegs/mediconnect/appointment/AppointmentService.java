package com.vegs.mediconnect.appointment;

import com.vegs.mediconnect.doctor.Doctor;
import com.vegs.mediconnect.doctor.DoctorRepository;
import com.vegs.mediconnect.patient.Patient;
import com.vegs.mediconnect.patient.PatientRepository;
import com.vegs.mediconnect.schedule.Schedule;
import com.vegs.mediconnect.schedule.ScheduleRepository;
import com.vegs.mediconnect.util.NotFoundException;
import com.vegs.mediconnect.util.ReferencedWarning;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


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

    public List<AppointmentDTO> findAll() {
        final List<Appointment> appointments = appointmentRepository.findAll(Sort.by("id"));
        return appointments.stream()
                .map(appointment -> mapToDTO(appointment, new AppointmentDTO()))
                .toList();
    }

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
//        appointmentDTO.setPAappointmentId(appointment.getPAappointmentId() == null ? null : appointment.getPAappointmentId().getId());
//        appointmentDTO.setDAappointmentId(appointment.getDAappointmentId() == null ? null : appointment.getDAappointmentId().getId());
        return appointmentDTO;
    }

    private Appointment mapToEntity(final AppointmentDTO appointmentDTO,
            final Appointment appointment) {
        appointment.setStatus(appointmentDTO.getStatus());
        appointment.setAvailable(appointmentDTO.getAvailable());
        final Patient pAappointmentId = appointmentDTO.getPAappointmentId() == null ? null : patientRepository.findById(appointmentDTO.getPAappointmentId())
                .orElseThrow(() -> new NotFoundException("pAappointmentId not found"));
//        appointment.setPAappointmentId(pAappointmentId);
        final Doctor dAappointmentId = appointmentDTO.getDAappointmentId() == null ? null : doctorRepository.findById(appointmentDTO.getDAappointmentId())
                .orElseThrow(() -> new NotFoundException("dAappointmentId not found"));
//        appointment.setDAappointmentId(dAappointmentId);
        return appointment;
    }

    public ReferencedWarning getReferencedWarning(final UUID id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        final Schedule sAscheduleIdSchedule = scheduleRepository.findFirstBysAscheduleId(appointment);
//        if (sAscheduleIdSchedule != null) {
//            referencedWarning.setKey("appointment.schedule.sAscheduleId.referenced");
//            referencedWarning.addParam(sAscheduleIdSchedule.getId());
//            return referencedWarning;
//        }
        return null;
    }

}
