package com.vegs.mediconnect.appointment;

import com.vegs.mediconnect.doctor.Doctor;
import com.vegs.mediconnect.patient.Patient;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

//    Appointment findFirstBypAappointmentId(Patient patient);

//    Appointment findFirstBydAappointmentId(Doctor doctor);

}
