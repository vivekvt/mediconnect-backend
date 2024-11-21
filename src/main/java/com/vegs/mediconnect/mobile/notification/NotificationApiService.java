package com.vegs.mediconnect.mobile.notification;

import com.vegs.mediconnect.datasource.notification.NotificationPatientRepository;
import com.vegs.mediconnect.datasource.notification.NotificationRepository;
import com.vegs.mediconnect.datasource.patient.PatientRepository;
import com.vegs.mediconnect.mobile.notification.model.NotificationResponse;
import com.vegs.mediconnect.mobile.patient.PatientNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationApiService {

    private final NotificationPatientRepository notificationPatientRepository;
    private final PatientRepository patientRepository;

    public List<NotificationResponse> getNotifications(String patientEmail) {
        var patient = patientRepository.findByEmail(patientEmail)
                .orElseThrow(PatientNotFoundException::new);

        var notifications = notificationPatientRepository.findAllByPatientId(patient);
        return notifications.stream()
                .map(notificationPatient -> NotificationResponse
                        .builder()
                        .id(notificationPatient.getId())
                        .title(notificationPatient.getNotificationId().getTitle())
                        .message(notificationPatient.getNotificationId().getMessage())
                        .build())
                .toList();
    }

    public void ackNotification(UUID notificationPatientId) {
        var notification = notificationPatientRepository.findById(notificationPatientId)
                .orElseThrow(NotificationNotFoundException::new);

        notification.setAcknowledged(true);
        notificationPatientRepository.save(notification);
    }

}
