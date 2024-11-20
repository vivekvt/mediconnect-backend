package com.vegs.mediconnect.datasource.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationPatientRepository extends JpaRepository<NotificationPatient, UUID> {

    List<NotificationPatient> findAllByNotificationId(Notification notification);

}
