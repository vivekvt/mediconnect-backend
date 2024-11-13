package com.vegs.mediconnect.schedule_time;

import com.vegs.mediconnect.schedule.Schedule;
import com.vegs.mediconnect.schedule.ScheduleRepository;
import com.vegs.mediconnect.util.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ScheduleTimeService {

    private final ScheduleTimeRepository scheduleTimeRepository;
    private final ScheduleRepository scheduleRepository;

    public ScheduleTimeService(final ScheduleTimeRepository scheduleTimeRepository,
            final ScheduleRepository scheduleRepository) {
        this.scheduleTimeRepository = scheduleTimeRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<ScheduleTimeDTO> findAll() {
        final List<ScheduleTime> scheduleTimes = scheduleTimeRepository.findAll(Sort.by("id"));
        return scheduleTimes.stream()
                .map(scheduleTime -> mapToDTO(scheduleTime, new ScheduleTimeDTO()))
                .toList();
    }

    public ScheduleTimeDTO get(final UUID id) {
        return scheduleTimeRepository.findById(id)
                .map(scheduleTime -> mapToDTO(scheduleTime, new ScheduleTimeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final ScheduleTimeDTO scheduleTimeDTO) {
        final ScheduleTime scheduleTime = new ScheduleTime();
        mapToEntity(scheduleTimeDTO, scheduleTime);
        return scheduleTimeRepository.save(scheduleTime).getId();
    }

    public void update(final UUID id, final ScheduleTimeDTO scheduleTimeDTO) {
        final ScheduleTime scheduleTime = scheduleTimeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(scheduleTimeDTO, scheduleTime);
        scheduleTimeRepository.save(scheduleTime);
    }

    public void delete(final UUID id) {
        scheduleTimeRepository.deleteById(id);
    }

    private ScheduleTimeDTO mapToDTO(final ScheduleTime scheduleTime,
            final ScheduleTimeDTO scheduleTimeDTO) {
        scheduleTimeDTO.setId(scheduleTime.getId());
        scheduleTimeDTO.setTime(scheduleTime.getTime());
        scheduleTimeDTO.setAvailable(scheduleTime.getAvailable());
        scheduleTimeDTO.setSSTscheduleTimeId(scheduleTime.getSSTscheduleTimeId() == null ? null : scheduleTime.getSSTscheduleTimeId().getId());
        return scheduleTimeDTO;
    }

    private ScheduleTime mapToEntity(final ScheduleTimeDTO scheduleTimeDTO,
            final ScheduleTime scheduleTime) {
        scheduleTime.setTime(scheduleTimeDTO.getTime());
        scheduleTime.setAvailable(scheduleTimeDTO.getAvailable());
        final Schedule sSTscheduleTimeId = scheduleTimeDTO.getSSTscheduleTimeId() == null ? null : scheduleRepository.findById(scheduleTimeDTO.getSSTscheduleTimeId())
                .orElseThrow(() -> new NotFoundException("sSTscheduleTimeId not found"));
        scheduleTime.setSSTscheduleTimeId(sSTscheduleTimeId);
        return scheduleTime;
    }

}
