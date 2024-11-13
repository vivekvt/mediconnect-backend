package com.vegs.mediconnect.schedule;

import com.vegs.mediconnect.appointment.Appointment;
import com.vegs.mediconnect.doctor.Doctor;
import com.vegs.mediconnect.schedule_time.ScheduleTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Schedule {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "UUID")
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column
    private LocalDate date;

    @Column
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_sschedule_id_id", nullable = false)
    private Doctor dSscheduleId;

    @OneToMany(mappedBy = "sSTscheduleTimeId")
    private Set<ScheduleTime> sSTscheduleId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_aschedule_id_id", nullable = false, unique = true)
    private Appointment sAscheduleId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
