package com.vegs.mediconnect.patient;

import com.vegs.mediconnect.appointment.Appointment;
import com.vegs.mediconnect.appointment.AppointmentRepository;
import com.vegs.mediconnect.util.NotFoundException;
import com.vegs.mediconnect.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public PatientService(final PatientRepository patientRepository,
            final AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<PatientDTO> findAll() {
        final List<Patient> patients = patientRepository.findAll(Sort.by("id"));
        return patients.stream()
                .map(patient -> mapToDTO(patient, new PatientDTO()))
                .toList();
    }

    public PatientDTO get(final String id) {
        return patientRepository.findById(id)
                .map(patient -> mapToDTO(patient, new PatientDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final PatientDTO patientDTO) {
        final Patient patient = new Patient();
        mapToEntity(patientDTO, patient);
        patient.setId(patientDTO.getId());
        return patientRepository.save(patient).getId();
    }

    public void update(final String id, final PatientDTO patientDTO) {
        final Patient patient = patientRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(patientDTO, patient);
        patientRepository.save(patient);
    }

    public void delete(final String id) {
        patientRepository.deleteById(id);
    }

    private PatientDTO mapToDTO(final Patient patient, final PatientDTO patientDTO) {
        patientDTO.setId(patient.getId());
        patientDTO.setFirstName(patient.getFirstName());
        patientDTO.setLastName(patient.getLastName());
        return patientDTO;
    }

    private Patient mapToEntity(final PatientDTO patientDTO, final Patient patient) {
        patient.setFirstName(patientDTO.getFirstName());
        patient.setLastName(patientDTO.getLastName());
        return patient;
    }

    public boolean idExists(final String id) {
        return patientRepository.existsByIdIgnoreCase(id);
    }

    public ReferencedWarning getReferencedWarning(final String id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Patient patient = patientRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        final Appointment pAappointmentIdAppointment = appointmentRepository.findFirstBypAappointmentId(patient);
//        if (pAappointmentIdAppointment != null) {
//            referencedWarning.setKey("patient.appointment.pAappointmentId.referenced");
//            referencedWarning.addParam(pAappointmentIdAppointment.getId());
//            return referencedWarning;
//        }
        return null;
    }

}
