package com.vegs.mediconnect.patient;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PatientRepository extends JpaRepository<Patient, String> {

    boolean existsByIdIgnoreCase(String id);

}
