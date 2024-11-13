package com.vegs.mediconnect.doctor;

import com.vegs.mediconnect.util.ReferencedException;
import com.vegs.mediconnect.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
public class DoctorResource {

    private final DoctorService doctorService;

    public DoctorResource(final DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctor(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(doctorService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createDoctor(@RequestBody @Valid final DoctorDTO doctorDTO) {
        final String createdId = doctorService.create(doctorDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateDoctor(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final DoctorDTO doctorDTO) {
        doctorService.update(id, doctorDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDoctor(@PathVariable(name = "id") final String id) {
        final ReferencedWarning referencedWarning = doctorService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
