package com.vegs.mediconnect.schedule;

import com.vegs.mediconnect.appointment.Appointment;
import com.vegs.mediconnect.appointment.AppointmentRepository;
import com.vegs.mediconnect.doctor.Doctor;
import com.vegs.mediconnect.doctor.DoctorRepository;
import com.vegs.mediconnect.schedule_time.ScheduleTime;
import com.vegs.mediconnect.schedule_time.ScheduleTimeRepository;
import com.vegs.mediconnect.util.NotFoundException;
import com.vegs.mediconnect.util.ReferencedWarning;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final ScheduleTimeRepository scheduleTimeRepository;

    public ScheduleService(final ScheduleRepository scheduleRepository,
            final DoctorRepository doctorRepository,
            final AppointmentRepository appointmentRepository,
            final ScheduleTimeRepository scheduleTimeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.scheduleTimeRepository = scheduleTimeRepository;
    }

    public List<ScheduleDTO> findAll() {
        final List<Schedule> schedules = scheduleRepository.findAll(Sort.by("id"));
        return schedules.stream()
                .map(schedule -> mapToDTO(schedule, new ScheduleDTO()))
                .toList();
    }

    public ScheduleDTO get(final UUID id) {
        return scheduleRepository.findById(id)
                .map(schedule -> mapToDTO(schedule, new ScheduleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final ScheduleDTO scheduleDTO) {
        final Schedule schedule = new Schedule();
        mapToEntity(scheduleDTO, schedule);
        return scheduleRepository.save(schedule).getId();
    }

    public void update(final UUID id, final ScheduleDTO scheduleDTO) {
        final Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(scheduleDTO, schedule);
        scheduleRepository.save(schedule);
    }

    public void delete(final UUID id) {
        scheduleRepository.deleteById(id);
    }

    private ScheduleDTO mapToDTO(final Schedule schedule, final ScheduleDTO scheduleDTO) {
        scheduleDTO.setId(schedule.getId());
        scheduleDTO.setDate(schedule.getDate());
        scheduleDTO.setAvailable(schedule.getAvailable());
        scheduleDTO.setDSscheduleId(schedule.getDoctor() == null ? null : schedule.getDoctor().getId());
//        scheduleDTO.setSAscheduleId(schedule.getSAscheduleId() == null ? null : schedule.getSAscheduleId().getId());
        return scheduleDTO;
    }

    private Schedule mapToEntity(final ScheduleDTO scheduleDTO, final Schedule schedule) {
        schedule.setDate(scheduleDTO.getDate());
        schedule.setAvailable(scheduleDTO.getAvailable());
        final Doctor dSscheduleId = scheduleDTO.getDSscheduleId() == null ? null : doctorRepository.findById(scheduleDTO.getDSscheduleId())
                .orElseThrow(() -> new NotFoundException("dSscheduleId not found"));
        schedule.setDoctor(dSscheduleId);
        final Appointment sAscheduleId = scheduleDTO.getSAscheduleId() == null ? null : appointmentRepository.findById(scheduleDTO.getSAscheduleId())
                .orElseThrow(() -> new NotFoundException("sAscheduleId not found"));
//        schedule.setSAscheduleId(sAscheduleId);
        return schedule;
    }

//    public boolean sAscheduleIdExists(final UUID id) {
//        return scheduleRepository.existsBysAscheduleIdId(id);
//    }

    public ReferencedWarning getReferencedWarning(final UUID id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        final ScheduleTime sSTscheduleTimeIdScheduleTime = scheduleTimeRepository.findFirstBysSTscheduleTimeId(schedule);
//        if (sSTscheduleTimeIdScheduleTime != null) {
//            referencedWarning.setKey("schedule.scheduleTime.sSTscheduleTimeId.referenced");
//            referencedWarning.addParam(sSTscheduleTimeIdScheduleTime.getId());
//            return referencedWarning;
//        }
        return null;
    }

}
