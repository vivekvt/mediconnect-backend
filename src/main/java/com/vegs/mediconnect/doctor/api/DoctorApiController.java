package com.vegs.mediconnect.doctor.api;

import com.vegs.mediconnect.doctor.api.model.DoctorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping(value = "/api/mobile/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DoctorApiController {

    private final DoctorApiService doctorApiService;


    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctor(@PathVariable(name = "id") final UUID id) {
        return ResponseEntity.ok(doctorApiService.getDoctorDetails(id));
    }

}
