package com.vegs.mediconnect.schedule;

import com.vegs.mediconnect.doctor.Doctor;
import com.vegs.mediconnect.doctor.DoctorRepository;
import com.vegs.mediconnect.util.NotFoundException;
import com.vegs.mediconnect.util.ReferencedWarning;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    public ScheduleService(final ScheduleRepository scheduleRepository,
            final DoctorRepository doctorRepository) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public List<ScheduleDTO> findAll() {
        final List<Schedule> schedules = scheduleRepository.findAll(Sort.by("id"));
        return schedules.stream()
                .map(schedule -> mapToDTO(schedule, new ScheduleDTO()))
                .toList();
    }

    @Transactional
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
        scheduleDTO.setDoctorId(schedule.getDoctor().getId());
        scheduleDTO.setDoctorName(schedule.getDoctor().getFullName());
        return scheduleDTO;
    }

    private Schedule mapToEntity(final ScheduleDTO scheduleDTO, final Schedule schedule) {
        schedule.setDate(scheduleDTO.getDate());
        schedule.setAvailable(scheduleDTO.getAvailable());
        schedule.setDoctor(doctorRepository.getReferenceById(scheduleDTO.getDoctorId()));
        return schedule;
    }

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
