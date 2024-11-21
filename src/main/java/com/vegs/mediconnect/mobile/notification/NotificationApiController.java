package com.vegs.mediconnect.mobile.notification;

import com.vegs.mediconnect.mobile.notification.model.AcknowledgeNotificationRequest;
import com.vegs.mediconnect.mobile.notification.model.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/mobile/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationApiService notificationApiService;

    @GetMapping("/{email}")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
            @PathVariable(name = "email") final String email) {
        return ResponseEntity.ok(notificationApiService.getNotifications(email));
    }

    @PostMapping
    public ResponseEntity<Void> acknowledgeNotification(AcknowledgeNotificationRequest ackNotificationRequest) {
        return ResponseEntity.accepted().build();
    }

}
