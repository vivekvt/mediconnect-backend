package com.vegs.mediconnect.doctor;

import org.springframework.data.jpa.repository.JpaRepository;


public interface DoctorRepository extends JpaRepository<Doctor, String> {

    boolean existsByIdIgnoreCase(String id);

}
