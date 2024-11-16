package com.vegs.mediconnect.schedule_time;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface ScheduleTimeRepository extends JpaRepository<ScheduleTime, UUID> {
}
