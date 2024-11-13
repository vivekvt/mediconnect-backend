package com.vegs.mediconnect.doctor;

import com.vegs.mediconnect.appointment.Appointment;
import com.vegs.mediconnect.schedule.Schedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Doctor {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String experienceInYears;

    @Column(length = 10)
    private String score;

    @Column
    private String about;

    @OneToMany(mappedBy = "dSscheduleId")
    private Set<Schedule> dSdoctorId;

    @OneToMany(mappedBy = "dAappointmentId")
    private Set<Appointment> dAdoctorId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
