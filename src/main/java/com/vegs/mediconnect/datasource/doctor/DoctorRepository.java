package com.vegs.mediconnect.datasource.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

}
