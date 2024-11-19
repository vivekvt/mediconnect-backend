package com.vegs.mediconnect.doctor;

import com.vegs.mediconnect.appointment.AppointmentRepository;
import com.vegs.mediconnect.schedule.ScheduleRepository;
import com.vegs.mediconnect.util.NotFoundException;
import com.vegs.mediconnect.util.ReferencedWarning;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorService(final DoctorRepository doctorRepository,
            final ScheduleRepository scheduleRepository,
            final AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<DoctorDTO> findAll() {
        final List<Doctor> doctors = doctorRepository
                .findAll(Sort.by("lastName", "firstName"));
        return doctors.stream()
                .map(doctor -> mapToDTO(doctor, new DoctorDTO()))
                .toList();
    }

    public DoctorDTO get(final UUID id) {
        return doctorRepository.findById(id)
                .map(doctor -> mapToDTO(doctor, new DoctorDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final DoctorDTO doctorDTO) {
        final Doctor doctor = new Doctor();
        mapToEntity(doctorDTO, doctor);
        return doctorRepository.save(doctor).getId();
    }

    public void update(final UUID id, final DoctorDTO doctorDTO) {
        final Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(doctorDTO, doctor);
        doctorRepository.save(doctor);
    }

    public void delete(final UUID id) {
        doctorRepository.deleteById(id);
    }

    private DoctorDTO mapToDTO(final Doctor doctor, final DoctorDTO doctorDTO) {
        doctorDTO.setId(doctor.getId());
        doctorDTO.setFirstName(doctor.getFirstName());
        doctorDTO.setLastName(doctor.getLastName());
        doctorDTO.setExperienceInYears(doctor.getExperienceInYears());
        doctorDTO.setAbout(doctor.getAbout());
        return doctorDTO;
    }

    private Doctor mapToEntity(final DoctorDTO doctorDTO, final Doctor doctor) {
        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setExperienceInYears(doctorDTO.getExperienceInYears());
        doctor.setAbout(doctorDTO.getAbout());
        doctor.setProfilePhoto(getBytes(doctorDTO.getImage()));
        return doctor;
    }

    private byte[] getBytes(MultipartFile image) {
        try {
            return image.getBytes();
        } catch (IOException e) {
            log.error("Error to get bytes from doctor.image", e);
        }
        return null;
    }

    public ReferencedWarning getReferencedWarning(final UUID id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        final Schedule dSscheduleIdSchedule = scheduleRepository.findFirstBydSscheduleId(doctor);
//        if (dSscheduleIdSchedule != null) {
//            referencedWarning.setKey("doctor.schedule.dSscheduleId.referenced");
//            referencedWarning.addParam(dSscheduleIdSchedule.getId());
//            return referencedWarning;
//        }
//        final Appointment dAappointmentIdAppointment = appointmentRepository.findFirstBydAappointmentId(doctor);
//        if (dAappointmentIdAppointment != null) {
//            referencedWarning.setKey("doctor.appointment.dAappointmentId.referenced");
//            referencedWarning.addParam(dAappointmentIdAppointment.getId());
//            return referencedWarning;
//        }
        return null;
    }

}
