package com.vegs.mediconnect.patient.api;

import com.vegs.mediconnect.doctor.api.model.DoctorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PatientApiControllerAdvice {

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<DoctorResponse> handlePatientNotFound(PatientNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }
}
