package com.vegs.mediconnect.doctor.api;

import com.vegs.mediconnect.doctor.api.model.DoctorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DoctorResourceAdvice {

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<DoctorResponse> handleDoctorNotFound(DoctorNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }
}
