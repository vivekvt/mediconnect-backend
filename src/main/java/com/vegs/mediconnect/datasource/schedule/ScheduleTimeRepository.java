package com.vegs.mediconnect.datasource.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ScheduleTimeRepository extends JpaRepository<ScheduleTime, UUID> {

    Optional<ScheduleTime> findByTimeAndScheduleId(LocalTime time, UUID scheduleId);
    List<ScheduleTime> findAllByScheduleId(UUID sheduleId);
}
