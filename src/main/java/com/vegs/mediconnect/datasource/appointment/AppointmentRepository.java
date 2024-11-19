package com.vegs.mediconnect.datasource.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

}
