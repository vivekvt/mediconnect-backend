package com.vegs.mediconnect.patient;

import com.vegs.mediconnect.appointment.Appointment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.List;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Patient {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
