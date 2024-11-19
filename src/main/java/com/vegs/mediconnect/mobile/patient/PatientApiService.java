package com.vegs.mediconnect.mobile.patient;

import com.vegs.mediconnect.datasource.patient.Patient;
import com.vegs.mediconnect.datasource.patient.PatientRepository;
import com.vegs.mediconnect.mobile.patient.model.PatientDetailResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientApiService {

    private final PatientRepository patientRepository;

    public PatientDetailResponse getPatientByEmail(String email) {
        var patient = patientRepository.findByEmail(email)
                .orElseThrow(PatientNotFoundException::new);
        return mapToPatientDetailResponse(patient);
    }

    public UUID create(@Valid PatientDetailResponse patientDTO) {
        return patientRepository.save(mapToEntity(patientDTO)).getId();
    }

    public void update(UUID id, PatientDetailResponse patientDTO) {
        var patient = patientRepository.findById(id)
                .orElseThrow(PatientNotFoundException::new);
        mapToEntity(patientDTO, patient);
        patientRepository.save(patient);
    }

    private void mapToEntity(PatientDetailResponse patientDetailResponse, Patient patient) {
        patient.setId(patientDetailResponse.getId());
        patient.setEmail(patientDetailResponse.getEmail());
        patient.setClinicCode(patientDetailResponse.getClinicCode());
        patient.setFirstName(patientDetailResponse.getFirstName());
        patient.setLastName(patientDetailResponse.getLastName());
        patient.setGender(patientDetailResponse.getGender());
        patient.setBirthdate(patientDetailResponse.getBirthdate());
        patient.setPhoneNumber(patientDetailResponse.getPhoneNumber());
        patient.setAddress(patientDetailResponse.getAddress());
    }

    private Patient mapToEntity(PatientDetailResponse patientDetailResponse) {
        return Patient
                .builder()
                .id(patientDetailResponse.getId())
                .email(patientDetailResponse.getEmail())
                .clinicCode(patientDetailResponse.getClinicCode())
                .firstName(patientDetailResponse.getFirstName())
                .lastName(patientDetailResponse.getLastName())
                .gender(patientDetailResponse.getGender())
                .birthdate(patientDetailResponse.getBirthdate())
                .phoneNumber(patientDetailResponse.getPhoneNumber())
                .address(patientDetailResponse.getAddress())
                .build();
    }

    private PatientDetailResponse mapToPatientDetailResponse(Patient patient) {
        return PatientDetailResponse
                .builder()
                .id(patient.getId())
                .email(patient.getEmail())
                .clinicCode(patient.getClinicCode())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .fullName(patient.getFullName())
                .gender(patient.getGender())
                .birthdate(patient.getBirthdate())
                .phoneNumber(patient.getPhoneNumber())
                .address(patient.getAddress())
                .build();
    }
}
