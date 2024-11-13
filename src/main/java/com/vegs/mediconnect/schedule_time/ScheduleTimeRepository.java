package com.vegs.mediconnect.schedule_time;

import com.vegs.mediconnect.schedule.Schedule;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleTimeRepository extends JpaRepository<ScheduleTime, UUID> {

    ScheduleTime findFirstBysSTscheduleTimeId(Schedule schedule);

}
