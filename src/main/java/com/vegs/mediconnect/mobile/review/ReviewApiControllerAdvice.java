package com.vegs.mediconnect.mobile.review;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReviewApiControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleAppointmentNotFound(ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body("Appointment already reviewed");
    }
}
