package com.vegs.mediconnect.mobile.notification;

import com.vegs.mediconnect.mobile.notification.model.NotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotificationApiControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<NotificationRequest> handleNotificationNotFound(RuntimeException exception) {
        return ResponseEntity.badRequest().build();
    }
}
