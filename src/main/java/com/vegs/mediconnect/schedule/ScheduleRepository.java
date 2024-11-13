package com.vegs.mediconnect.schedule;

import com.vegs.mediconnect.appointment.Appointment;
import com.vegs.mediconnect.doctor.Doctor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    Schedule findFirstBydSscheduleId(Doctor doctor);

    Schedule findFirstBysAscheduleId(Appointment appointment);

    boolean existsBysAscheduleIdId(UUID id);

}
